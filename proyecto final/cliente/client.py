import threading
from menu import Menu
from directory import Directory, File
from request_handler import RequestHandler
from connection import DBManager


if __name__ == "__main__":

    try:
        requestHandler = RequestHandler()
    except Exception:
        print("Error: Request Handler error")

    menu = Menu()
    directory = Directory("/app/data")
    log = DBManager()

    #hilos controlador de archivos
    thread_getLocalFiles = threading.Thread(target=directory.getFilesDirectory, args=(requestHandler,))
    thread_getLocalFiles.start()

    #hilo controlador del contenido
    thread_updateFiles = threading.Thread(target=directory.getContentDirectory, args=(requestHandler,))
    thread_updateFiles.start()
    

    while (menu.getOption() != 0):
        menu.showTitle()
        menu.showOptions()
        
        try:
            menu.setOption(int(input(">> Selecciona una ópcion: ")))
            menu.clear_screen()

            if menu.getOption() == 1:
                
                menu.showTitle(title="ARCHIVOS COMPARTIDOS")
                listFiles = requestHandler.getPublicFiles()

                for file in listFiles:
                    print(f"File: {file['name']}")
                input("")
            elif menu.getOption() == 2:

                menu.showTitle(title="ELIMINAR ARCHIVO")

                listFiles = requestHandler.getPublicFiles()
                for file in listFiles:
                    print(f"File: {file['name']}")

                name = (str(input(">> Digita el nombre del archivo a eliminar : ")))
                fileExist = False
                valido = True

                fileExist = requestHandler.findPublicFile(name)

                if fileExist:
                    noValido = requestHandler.getEdits()
                    if noValido != None:
                        for item in noValido:
                            if name == item["name"]:
                                valido = False

                if valido and fileExist:
                    requestHandler.deleteFile(name)
                    directory.deleteFileByName(name)
                    
                    log.delate_file(name,"el usuario elimino el archivo")

                    input("archivo " + name + " eliminado")
                else:
                    input("error al eliminar el archivo " + name)

            elif menu.getOption() == 3:
                
                menu.showTitle(title="MODIFICAR ARCHIVOS")

                listFiles = requestHandler.getPublicFiles()
                for file in listFiles:
                    print(file["name"] + " - " + "'" + file["content"] + "'")

                name = (str(input(">> Digita el nombre del archivo: "))) #Revisar la opcion de revisar 
                
                fileExist = requestHandler.findPublicFile(name)

                if fileExist:
                    requestHandler.editando(name, True) # decimos que el archivo se esta editando
                    
                    menu.clear_screen()
                    menu.showTitle(name)
                    newContent = (str(input(">> Escribe el nuevo contenido del archivo: ")))
                    
                    requestHandler.updateContentFile(name,content=newContent)
                    requestHandler.editando(name, False)

                    log.update_content_file(name, ("el usuario cambio el contenido a : " + newContent))

            elif menu.getOption() == 4:

                menu.showTitle(title="ARCHIVOS LOCALES")

                listFiles = directory.getFilesInfoCopy() #visualizar todos los archivos publicos 
                
                for file in listFiles:
                    print(file)
            
            elif menu.getOption() == 5:

                menu.showTitle(title="MODIFICAR ESTADO PUBLICO")

                list = directory.getFilesInfoCopy()
                for file in list:
                    print(file["name"] + "  -  " + str(file["edit"]))
                
                name = input(">> ingresa el nombre del archivo que quieres cambiar su estado:  ")
                newState = directory.publicStateByName(name)

                if newState == False:
                    requestHandler.deleteFile(name)
                    temp = directory.getInfoByName(name)
                    directory.updateFileContentByName(temp["name"],False,temp["content"])

                    log.update_content_file(name,"el archivo " + name + " ahora es privado")

                if newState == True:
                    archivoLocal = directory.getInfoByName(name)

                    temp = File(archivoLocal["name"],False,archivoLocal["content"])
                    requestHandler.addFile(temp.getFormatJSON())
                    directory.updateFileContentByName(archivoLocal["name"],True,archivoLocal["content"])
                    
                    log.update_content_file(name,"el archivo " + name + " ahora es publico")
                input("")

            elif menu.getOption() == 0: 
                print("Saliendo del programa...")
                directory.stop_threads()
                break
            else:
                print("Opción no válida. Por favor, selecciona una opción válida.")

        except ValueError:
            print("Error: Por favor, ingresa un numero entero valido")