import Pyro4
import pprint

name_server = Pyro4.locateNS(host="192.168.1.65", port=9090)
uri = name_server.lookup("system")

server = Pyro4.Proxy(uri)

#Codigo para mostrar la lista de funciones que estan expuestas dentro del servicion de nombres 

try:
    file = {
        "name": "anotherFile.txt",
        "content": "Este es otro archivo",
        "publicState": False
    }

    print(server.addFile(file))
    
except Exception:
    print("Ups ha ocurrido algo inesperado \n".join(Pyro4.util.getPyroTraceback()))