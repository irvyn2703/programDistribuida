import os 
from connection import *

class Menu:

    def __init__(self):
        self._option = -1
        self.__controller = DBManager()

    def showTitle(self):
        print("-----------------------------------")
        print("\tSISTEMA DE ARCHIVOS")
        print("-----------------------------------")
    
    def showOptions(self):
        print("[ 1 ] Ver archivos")
        print("[ 2 ] Eliminar Archivo")
        print("[ 3 ] Modificar Archivos")
        print("[ 4 ] Ver archivos locales")
        print("[ 5 ] Modificar estado público del archivo")
        print("[ 0 ] Salir")
    
    def clear_screen(self):
        # For Windows
        if os.name == 'nt':
            _ = os.system('cls')
        # For Unix/Linux/Mac
        else:
            _ = os.system('clear')

    def getOption(self):
        return self._option

    def setOption(self, option):
        self._option = option
    
    def getController(self):
        return self.__controller
    


    