# los scripts deben ser probados desde laterminal de la siguiente manera:
# mpiexec -n x python mpi4py_script_name.py

from mpi4py import MPI
comm = MPI.COMM_WORLD
rank = comm.Get_rank()
print("hello world from process ", rank)