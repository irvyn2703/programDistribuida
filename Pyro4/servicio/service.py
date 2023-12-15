import Pyro4
from connection import *

""" Servicio de gestion de archivos """

class PyroService(object):

    #Metodos no disponibles 
    def __init__(self): #constructor 
        self.__controller = Controller()

    #Metodos disponibles para acceso remoto
    @Pyro4.expose
    def addFile(self,file): #Metodo para agregar un archivo dentro de la lista
        return self.__controller.add(file)
    
    @Pyro4.expose
    def getFiles(self): #Retorna la lista de archivos 
        return self.__controller.getAll()
    
    @Pyro4.expose
    def update(self,nameFile, content):  
        self.__controller.update(nameFile, content)
    
    @Pyro4.expose
    def delateFile(self,nameFile):
        self.__controller.delate(nameFile)

    @Pyro4.expose
    def editando(self,nameFile, content):
        self.__controller.updateEdit(nameFile, content)

    @Pyro4.expose
    def getEdits(self):
        self.__controller.getEdits()
        
    

def main():
    print("--- Servidor en escucha ---")

    daemon = Pyro4.Daemon(host="127.0.0.1" ,port=5000) #colocamos el deamon en una direccion y purto estatica 
    ns = Pyro4.locateNS(host="127.0.0.1", port=9090) #localizamos el servidor de nombres en este puerto 
    uri = daemon.register(PyroService)
    ns.register("system.Files",uri) #Registro dentro del servicio de nombres 

    daemon.requestLoop() #En espera de llamadas remotas 
    


#metodo principal
if __name__ == "__main__":
    main()


#Agregar checksum 