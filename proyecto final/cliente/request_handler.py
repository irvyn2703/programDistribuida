import Pyro4

class RequestHandler:

    def __init__(self):
        self._name_server = Pyro4.locateNS(host="172.22.128.1", port=9090)
        self._uri = self._name_server.lookup("system")
        self._server = Pyro4.Proxy(self._uri)

    def getPublicFiles(self):
        try:
            return self._server.getFiles()
        except Exception:
            print("Ups ha ocurrido algo inesperado.\n".join(Pyro4.util.getPyroTraceback()))

    def addFile(self, file):
        try:
            return self._server.addFile(file)
        except Exception:
            print("Ups ha ocurrido algo inesperado.\n".join(Pyro4.util.getPyroTraceback()))
    
    def findPublicFile(self,name):
        try:
            foundedFile = self._server.findFileByName(name)
            if foundedFile != None:
                return True
            else:
                return False 
            
        except Exception:
            print("Ups ha ocurrido algo inesperado.\n".join(Pyro4.util.getPyroTraceback()))
    
    def deleteFile(self,nameFile):
        try:
            return self._server.deleteFile(nameFile)
        except Exception:
            print("Ups ha ocurrido algo inesperado.\n".join(Pyro4.util.getPyroTraceback()))
    
    def updateContentFile(self, nameFile, content):
        try:
            return self._server.update(nameFile,content)
        except Exception:
            print("Ups ha ocurrido algo inesperado.\n".join(Pyro4.util.getPyroTraceback()))
    

    def getEdits(self):
        try:
            return self._server.getEdits()
        except Exception:
            print("Ups ha ocurrido algo inesperado.\n".join(Pyro4.util.getPyroTraceback()))


    def editando(self, nameFile, value_edit):
        try:
            return self._server.editando(nameFile,value_edit)
        except Exception:
            print("Ups ha ocurrido algo inesperado.\n".join(Pyro4.util.getPyroTraceback()))
