import numpy as np
from mpi4py import MPI

comm = MPI.COMM_WORLD
size = comm.size
rank = comm.rank

array_size = 1  # Tamaño del arreglo local
recvdata = np.zeros(array_size, dtype=np.int64)
senddata = np.arange(array_size, dtype=np.int64) + 1 

print("Proceso %s envia %s " % (rank, senddata))

# Realizar una reducción en la suma utilizando la operación MPI.SUM
comm.Reduce(senddata, recvdata, root=0, op=MPI.SUM)

# El proceso raíz imprime el resultado final
if rank == 0:
    print('En el proceso', rank, 'después de la reduccion:   data =', recvdata)
