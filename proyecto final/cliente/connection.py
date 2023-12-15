from pymongo import MongoClient
from datetime import datetime


class Factory: 

    def __init__(self):
        self._client = MongoClient("mongodb://localhost:27017") 
    
    def getCollection(self, collection="files"): 
        db = self._client["db_files"]
        return db[collection]


class Operation:

    def __init__(self, operation, file_name, comment):
        connection = Factory()
        self.collection = connection.getCollection(collection="log")

        self.collection.insert_one({
            "timestamp": datetime.utcnow(),
            "operation": operation,
            "file_name": file_name,
            "comment": comment })

class DBManager:

    def __init__(self):
        pass

    def create_file(self, file):
        try:
            Operation("Create", file.name, file.content)
        except Exception as error:
            print(f"Error: {error}")
      
    def update_content_file(self,file_name, comment):
        try:
            Operation("Update", file_name, comment)
        except Exception as error:
            print(f"Error: {error}")
    
    def delate_file(self, file_name, comment):
        try:
            Operation("Delete",file_name,comment)
        except Exception as error:
            print(f"Error: {error}")
            