# nombre  /  anomalo/ ini  / fin





class normal():
    def __init__(self, nombre, tipo):
        self.name = nombre
        self.type = tipo


class section():
    def __init__(self, inicio, duracion):
        self.inicio = inicio
        self.duracion = duracion


class anomalous(normal):
    def __init__(self, nombre, tipo, listaTramosAnomalos=[], listaTramosNoUtiles=[]):
        normal.__init__(self, nombre, tipo)
        self.tramosAnomalos = listaTramosAnomalos
        self.tramos_no_usar = listaTramosNoUtiles


def leer(src_normal, src_anomalous):
    lista1, lista2 = [], []
    arch1 = open(src_normal, 'r')
    for line in arch1:
        line = line[:-1]
        v_n = normal(line, 0)
        lista1.append(v_n)
    arch1.close()
    arch2 = open(src_anomalous, 'r')
    for line in arch2:
        linea = line.split('/')
        #print(linea)
        v_n = anomalous(linea[0], 1)

        linea_no_usar = linea[1].split(" ")
        tramosNoUsar = []
        #print(linea_no_usar)
        i = 0
        while i < len(linea_no_usar)-1:
            tramosNoUsar.append(
                section(linea_no_usar[i], linea_no_usar[i+1]))
            i += 2
        v_n.tramos_no_usar = tramosNoUsar

        linea_anomalos = linea[2].split(" ")
        #print(linea_anomalos)
        tramosAnomalos = []
        i = 0
        while i < len(linea_anomalos)-1:
            tramosAnomalos.append(
                section(linea_anomalos[i], linea_anomalos[i+1]))
            i += 2
        v_n.tramosAnomalos = tramosAnomalos
        lista2.append(v_n)
    arch1.close()
    return lista1, lista2


#l1, l2 = leer(rt.n_tr_data_txt, rt.a_tr_data_txt)

""" for i in l2:
    print(i.name, i.type, end=" ")
    print("-", end="")
    for t in i.tramos_no_usar:
        print(t.inicio, t.duracion, end=" ")
    print("-", end="")
    for t in i.tramosAnomalos:
        print(t.inicio, t.duracion, end=" ")
    print("")
print(rt.a_tr_data_txt) """
