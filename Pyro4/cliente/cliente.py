from menu import Menu
from connection import DBManager
from directory import *
import threading
import Pyro4

name_server = Pyro4.locateNS()
uri = name_server.lookup("system.Files")

server = Pyro4.Proxy(uri)

if __name__ == "__main__":

    controller = DBManager()
    menu = Menu()
    directory = Directory("C:\\Users\\irvyn\\OneDrive\\Documents\\proyectoFinal\\carpetaCompartir") #direccion del directorio para compartir

    # hilo controlador de los archivos
    thread_get_files = threading.Thread(target=directory.get_files_directory_thread, args=(server,))
    thread_get_files.start()

    # hilo controlador del contenido de los archivos
    thread_update_files = threading.Thread(target=directory.get_content_directory_thread, args=(server,))
    thread_update_files.start()
    
    while (menu.getOption() != 0):
        menu.showTitle()
        menu.showOptions()
        
        try:
            menu.setOption(int(input(">> Selecciona una ópcion: ")))
            menu.clear_screen()


            if menu.getOption() == 1:
                print("-----------------------------------")
                print("\tArchivos Compartidos")
                print("-----------------------------------")
                list = server.getFiles()
                for file in list:
                    print(file["name"])
                input("")
            elif menu.getOption() == 2:
                print("-----------------------------------")
                print("\tEliminar Archivo")
                print("-----------------------------------")
                list = server.getFiles()
                for file in list:
                    print(file["name"])
                name = input(">> ingresa el nombre del archivo a eliminar: ")
                exist = False
                valido = True
                list = server.getFiles()
                for item in list:
                    if item["name"] == name:
                        exist = True
                
                if exist:
                    noValido = server.getEdits()
                    if noValido != None:
                        for item in noValido:
                            if name == item["name"]:
                                valido = False

                if valido and exist:
                    server.delateFile(name)
                    directory.delete_file_by_name(name)
                    controller.delate_file(name,"el usuario elimino el archivo")
                    input("archivo " + name + " eliminado")
                else:
                    input("error al eliminar el archivo " + name)
            elif menu.getOption() == 3:
                print("-----------------------------------")
                print("\tModificar Archivos")
                print("-----------------------------------")
                list = server.getFiles()
                for file in list:
                    print(file["name"] + " - " + "'" + file["content"] + "'")

                name = input(">> ingresa el nombre del archivo a modificar: ")
                exist = False
                list = server.getFiles()
                for item in list:
                    if item["name"] == name:
                        exist = True

                if exist:
                    server.editando(name, True) # decimos que el archivo se esta editando
                    menu.clear_screen()
                    print("-----------------------------------")
                    print("\t" + name + "")
                    print("-----------------------------------")
                    newContent = input("escribe el nuevo contenido del archivo: ")
                    server.update(name,newContent)
                    server.editando(name, False)
                    controller.update_content_file(name, "el usuario cambio el contenido a : " + newContent)
            elif menu.getOption() == 4:
                print("-----------------------------------")
                print("\tArchivos Locales")
                print("-----------------------------------")
                list = directory.get_files_info_copy()
                for file in list:
                    print(file["name"])
                input("")
            elif menu.getOption() == 5:
                print("-----------------------------------")
                print("\tModificar Estado Publico")
                print("-----------------------------------")
                list = directory.get_files_info_copy()
                for file in list:
                    print(file["name"] + "  -  " + str(file["publicState"]))
                
                name = input(">> ingresa el nombre del archivo que quieres cambiar su estado:  ")
                newState = directory.public_state_by_name(name)

                if newState == False:
                    server.delateFile(name)
                    temp = directory.get_file_info_by_name(name)
                    directory.update_file_content_by_name(temp["name"],False,temp["content"])
                    controller.update_content_file(name,"el archivo " + name + " ahora es privado")

                if newState == True:
                    archivoLocal = directory.get_file_info_by_name(name)
                    temp = File(archivoLocal["name"],False,archivoLocal["content"])
                    server.addFile(temp.getFormatJSON())
                    directory.update_file_content_by_name(archivoLocal["name"],True,archivoLocal["content"])
                    controller.update_content_file(name,"el archivo " + name + " ahora es publico")
                input("")
            elif menu.getOption() == 0:
                directory.stop_threads()
                print("Saliendo del programa...")
                break
            else:
                print("Opción no válida. Por favor, selecciona una opción válida.")

        except ValueError:
            print("Error: Por favor, ingresa un numero entero valido")