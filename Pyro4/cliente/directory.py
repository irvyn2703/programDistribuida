import os
import threading
import time 


class File:

    def __init__(self, name, public_state, content):
        self.name = name 
        self.public_state = public_state
        self.content = content
    
    def getFormatJSON(self):
        return {
            "name" : self.name,
            "edit" : self.public_state,
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
    
    def get_files(self):
        files = []
        for file in self.files_info:
            files.append(File(file.name,file.public_state))
        
        return files
    
    def get_files_directory_thread(self,server):
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
                    list = server.getFiles()
                    inList = False
                    for item in list:
                        if item["name"] == file_name:
                            inList = True
            
                    if inList:
                        self.files_info.append({"name": file_name, "publicState": True, "content": content})
                        server.update(file_name, content)
                    else:
                        self.files_info.append({"name": file_name, "publicState": False, "content": content})

     
                for file_name in deleted_files:  
                    server.delateFile(file_name) 

                self.files_info = [file for file in self.files_info if file["name"] not in deleted_files]

            # Actualizar el conjunto de archivos anteriores
            previous_files = current_files

            time.sleep(self.TTL)  # TTL de 40 segundos

    def get_files_info_copy(self):
        with self.files_lock:
            return list(self.files_info)
        
    def get_file_info_by_name(self, file_name):
        with self.files_lock:
            for file in self.files_info:
                if file["name"] == file_name:
                    print(file)
                    return file.copy()  # Devolver una copia para evitar modificaciones externas
    
    def public_state_by_name(self, file_name):
        newState = None
        with self.files_lock:
            for file in self.files_info:
                if file["name"] == file_name:
                    file["publicState"] = not file["publicState"]
                    newState = file["publicState"]
                    return newState
        return newState
    
    def update_file_content_by_name(self, file_name, new_state, new_content):
        with self.files_lock:
            for file in self.files_info:
                if file["name"] == file_name:
                    file["content"] = new_content
                    file["publicState"] = new_state
                    break 

    def delete_file_by_name(self, file_name):
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

    def print_files_thread(self):
        while not self.stop_event.is_set():
            with self.files_lock:
                for file in self.files_info:
                    name, size = file
                    print(f"Nombre: {name}, Tamaño: {size} bytes")
                print("-------------------------")
            time.sleep(30)

    def get_content_directory_thread(self, server):
        while not self.stop_event.is_set():
            with self.files_lock:
                list = server.getFiles()
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
