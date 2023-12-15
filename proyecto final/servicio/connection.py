from pymongo import MongoClient


""" Clase encargada de crear conexiones con la base de datos """

class Factory: 

    #constructor de la clase
    def __init__(self):
        self._client = MongoClient("mongodb://localhost:27017") 
    
    #metodo que retorna la coleccion de objetos de nuestra base de datos 
    def getCollection(self): 
        db = self._client["db_files"]
        return db["files"]


""" Clase encargada de definir el comportamiento de los metodos de la clase controller """

class FileDAO:

    def __init__(self, connection):
        self._collection = connection.getCollection()

    #metodo para buscar un archivo 
    def findFile(self,name):
        return self._collection.find({"name":name})

    #metodo que inserta un objeto en la coleccion de nuestra base de datos 
    def add(self, file):
        return self._collection.insert_one(file).inserted_id
    
    #metodo que retorna todos lo archivos de nuestra base de datos 
    def getAll(self):
        return self._collection.find()
    
    #metodo que actuliza el contenido de un archivo (el archivo se localiza de acuerdo a su nombre)
    def update(self, name, content):
        self._collection.update_one(
            {"name": name},
            {"$set": {"content": content}})
    
    # metodo para editar el parametro edit (cuando el archivo se esta editando)
    def updateEdit(self, name, edit_value):
        self._collection.update_one(
            {"name": name},
            {"$set": {"edit": edit_value}}
        )

    # metodo para retornar si un archivo se esta editando o no
    def getFilesEdits(self):
        return self._collection.find({"edit": True})
        
    #metodo que elimina un archivo
    def delete(self, name):
        self._collection.delete_one({"name":name})

        


""" Clase encargada de definir las funciones para la interaccion con la base de datos """

class Controller:

    def __init__(self):
        connection = Factory()
        self.fileDAO = FileDAO(connection)
    
    def find(self, name):
        return self.fileDAO.findFile(name)
    
    def add(self, file):
        return self.fileDAO.add(file)

    def getAll(self):
        return self.fileDAO.getAll()
    
    def update(self,nameFile,content): 
        self.fileDAO.update(nameFile, content)
    
    def updateEdit(self, name, edit):
        self.fileDAO.updateEdit(name,edit)

    def getEdits(self):
        return self.fileDAO.getFilesEdits()
    
    def delete(self, name):
        self.fileDAO.delete(name)