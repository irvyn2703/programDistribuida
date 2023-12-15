import Pyro4
from connection import *

""" Servicio de gestion de archivos """

class PyroService(object):

    #Metodos no disponibles 
    def __init__(self): #constructor 
        self.__controller = Controller()

    #Metodos disponibles para acceso remoto

    @Pyro4.expose
    def findFileByName(self,name):
        return self.__controller.find(name)

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
    def editando(self,nameFile, edit):
        self.__controller.updateEdit(nameFile, edit)

    @Pyro4.expose
    def getEdits(self):
        return self.__controller.getEdits()
    
    @Pyro4.expose
    def deleteFile(self,nameFile):
        self.__controller.delete(nameFile)
    

def main():
    print("-------------------------------------")
    print("\tSERVICIO DE ARCHIVOS")
    print("-------------------------------------")
    print("Estado: activado")

    daemon = Pyro4.Daemon(host="192.168.1.73" ,port=5000) #colocamos el deamon en una direccion y purto estatica 
    ns = Pyro4.locateNS(host="192.168.1.73", port=9090) #localizamos el servidor de nombres en este puerto 
    uri = daemon.register(PyroService)
    ns.register("system",uri) #Registro dentro del servicio de nombres 

    daemon.requestLoop() #En espera de llamadas remotas 
    


#metodo principal
if __name__ == "__main__":
    main()
