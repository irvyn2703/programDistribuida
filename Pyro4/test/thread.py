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
            "publicState" : self.public_state,
            "content"  : self.content
        } 
        


class Directory:

    def __init__(self, path):
        self.path = path
        self.files_info = []
        self.files_lock = threading.Lock()  # Semáforo para controlar el acceso a self.files
        self.stop_event = threading.Event()
        self.TTL = 10
    
    def get_files_directory(self):
        while not self.stop_event.is_set():
            with os.scandir(self.path) as entries:
                files_info = []
                for entry in entries:
                    if entry.is_file():
                        file_info = (entry.name, entry.stat().st_size)
                        files_info.append(file_info)

                        with self.files_lock:
                            self.files_info = files_info

            time.sleep(self.TTL) #TTL de 40 segundos

    def print_files(self):
        while not self.stop_event.is_set():
            with self.files_lock:
                for file in self.files_info:
                    name, size = file
                    print(f"Nombre: {name}, Tamaño: {size} bytes")
                print("-------------------------")
            time.sleep(30)

    def stop_threads(self):
        self.stop_event.set()

if __name__ == "__main__":

    directory = Directory("C:\\Users\\Jesus\\Documents\\compartir")

    thread_get_files = threading.Thread(target=directory.get_files_directory)
    thread_print_files = threading.Thread(target=directory.print_files)

    print("-------------------------")
    print("\tDirectorio")
    print("-------------------------")

    try:
        thread_get_files.start()
        thread_print_files.start()

        # Ejecutar hasta que se presiona Ctrl+C
        thread_get_files.join()
        thread_print_files.join()

    except KeyboardInterrupt:
        print("Proceso interrumpido por el usuario")

    finally:
        # Asegurarse de detener los hilos incluso si se produce una excepción
        directory.stop_threads()
