import os
import threading
import time 


class File:

    def __init__(self, name, edit,content):
        self.name = name 
        self.edit = edit
        self.content = content
    
    def getFormatJSON(self):
        return {
            "name" : self.name,
            "edit" : self.edit,
            "content"  : self.content
        } 
        
class Directory:

    def __init__(self, path):
        self.path = path
        self.files_info = []
        self.files_lock = threading.Lock()  # Semáforo para controlar el acceso a self.files
        self.stop_event = threading.Event()
        self.TTL = 5
        self.stop = False

    def getFilesDirectory(self,server):
        previous_files = set()

        while not self.stop_event.is_set():
            current_files = set()
            
            with os.scandir(self.path) as entries:
                for entry in entries:
                    if entry.is_file():
                        current_files.add(entry.name)

            # Identificar archivos nuevos y eliminados
            new_files = current_files - previous_files
            deleted_files = previous_files - current_files


            # Guardar nuevos archivos con publicState=False y eliminamos los archivos eliminados
            with self.files_lock:
                for file_name in new_files:
                    with open(os.path.join(self.path, file_name), "r") as file:
                        content = file.read()
                    
                    # verificamos si existe en el server
                    list = server.getPublicFiles()
                    inList = False
                    for item in list:
                        if item["name"] == file_name:
                            inList = True
            
                    if inList:
                        self.files_info.append({"name": file_name, "edit": True, "content": content})
                        server.updateContentFile(file_name, content)
                    else:
                        self.files_info.append({"name": file_name, "edit": False, "content": content})

     
                for file_name in deleted_files:  
                    server.deleteFile(file_name) 

                self.files_info = [file for file in self.files_info if file["name"] not in deleted_files]

            # Actualizar el conjunto de archivos anteriores
            previous_files = current_files

            time.sleep(self.TTL)  # TTL de 40 segundos

    def getFilesInfoCopy(self):
        with self.files_lock:
            return list(self.files_info)
    
    def getInfoByName(self, file_name):
        with self.files_lock:
            for file in self.files_info:
                if file["name"] == file_name:
                    print(file)
                    return file.copy()  # Devolver una copia para evitar modificaciones externas
    
    def publicStateByName(self, file_name):
        newState = None
        with self.files_lock:
            for file in self.files_info:
                if file["name"] == file_name:
                    file["edit"] = not file["edit"]
                    newState = file["edit"]
                    return newState
        return newState
    
    def updateFileContentByName(self, file_name, new_state, new_content):
        with self.files_lock:
            for file in self.files_info:
                if file["name"] == file_name:
                    file["content"] = new_content
                    file["edit"] = new_state
                    break 

    def deleteFileByName(self, file_name):
        with self.files_lock:
            for file in self.files_info:
                if file["name"] == file_name:
                    file_path = os.path.join(self.path, file_name)

                    # Eliminar el archivo del sistema de archivos
                    try:
                        os.remove(file_path)
                    except OSError as e:
                        print(f"Error al eliminar el archivo {file_name}: {e}")
                        return  # No continúes si hay un error al eliminar el archivo

                    # Eliminar el archivo de la lista en memoria
                    self.files_info.remove(file)
                    break

    def getContentDirectory(self, server):
        while not self.stop_event.is_set():
            with self.files_lock:
                list = server.getPublicFiles()
                for fileServer in list:
                    for fileLocal in self.files_info:
                        if fileServer["name"] == fileLocal["name"]:
                            if fileServer["content"] != fileLocal["content"]:
                                # Actualizar el contenido local con el contenido del servidor
                                fileLocal["content"] = fileServer["content"]

                                # abrir el archivo y escribir el nuevo contenido
                                file_path = os.path.join(self.path, fileLocal["name"])
                                with open(file_path, "w") as local_file:
                                    local_file.write(fileServer["content"])

            time.sleep(self.TTL)


    def stop_threads(self):
        self.stop_event.set()
