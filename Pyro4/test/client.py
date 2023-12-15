import Pyro4
import pprint

name_server = Pyro4.locateNS(host="192.168.1.65", port=9090)
uri = name_server.lookup("system")

server = Pyro4.Proxy(uri)

#Codigo para mostrar la lista de funciones que estan expuestas dentro del servicion de nombres 

try:

    list = []
    list = server.getFiles()

    for item in list:
        pprint.pprint(item)
    

except Exception:
    print("Ups ha ocurrido algo inesperado \n".join(Pyro4.util.getPyroTraceback()))