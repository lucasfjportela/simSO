package simuSO;

import memoria.*;

import java.util.Random;
import clock.*;

public class MMU implements Memoria, IClockListener {
    private int tempoAtual = 0;
	private MemoriaVirtual memVirtual;
    private MemoriaFisica memFisica;
    
    public MMU(int pTamanhoMemoriaFisica, int pTamanhoMemoriaVirtual){
    	this.memFisica = new MemoriaFisica(pTamanhoMemoriaFisica);
    	this.memVirtual = new MemoriaVirtual(pTamanhoMemoriaVirtual);
    }

    @Override
	public void escrever(int pIndiceVirtual, int idProcesso) { // Aplicar o algoritmo WS nesse m�todo pra saber quem vai sair ou ser substituido
    	
		boolean testePresenca = this.memVirtual.getPagina(pIndiceVirtual).presente();
		
		if (testePresenca == false) {
			PaginaVirtual pagina = memVirtual.getPagina(pIndiceVirtual);
			Random r = new Random();
			Integer valor = r.nextInt(100);
            Integer indiceMemoriaFisica = this.getIndiceMemFisica(this.memFisica.getMemoria()); 
			
			if(indiceMemoriaFisica == null){
				System.out.println("NAO TEM MAIS ESPA�O NA MEM FISICA");
				System.out.println("CHAMA O ALGORITMO WS");
				
			} else {
				pagina.setMolduraPagina(pIndiceVirtual, indiceMemoriaFisica);
	            pagina.modificar(true);
	            pagina.referenciar(true);
	            this.memFisica.setValor(indiceMemoriaFisica, valor);
	            System.out.println("Processo " + idProcesso + " escreveu valor " + valor + " no indice " + indiceMemoriaFisica + " da memoria fisica e"
	            		+ " escreveu " + indiceMemoriaFisica + " no indice " + pagina.getIndice() + " da memoria virtual!");
			}			
			
		} else {
			System.out.println("JA TEM PAGINA ENDERE�ADA NESSE INDICE DA MEMORIA VIRTUAL");
			return;
			//Integer indiceLivre = memFisica.getIndice();
            //System.out.println("P�gina livre : " + indiceLivre);
		}
	}

    @Override
	public void ler(int pIndice) {
    	try {
	    	if(this.memVirtual.getPagina(pIndice) == null) {
	    		return;
			} else {
				boolean t = this.memVirtual.getPagina(pIndice).presente();
				int pgFisica = this.memFisica.getValor(pIndice);
				
				if (t) {
					System.out.println("valor -> " + pgFisica);
				}
				else {
					System.out.println("n�o t� na memoria fisica");
				}
			}	
    	}catch (Exception e) {
			// TODO: handle exception
		}
	}   
    
    public Integer getIndiceMemFisica(Integer[] memFisica) {
		for(int i = 0; i < memFisica.length; i++){
			if(memFisica[i] == null){
				return i;
			}
		}
		return null;
	}
    
    // Para zerar os bits de referenciado das p�ginas virtuais
    public void eventoZerarRecebido(int tempoClock){
    	for(int i = 0; i < memVirtual.getTamanho(); i++){
    		memVirtual.getPagina(i).referenciar(false);
    	}
    	
    	this.tempoAtual = tempoClock;
    }    
}