package br.ce.wcaquino.buiders;

import br.ce.wcaquino.entidades.Filme;

public class FilmeBuider {
	
	private Filme filme;
	
	private FilmeBuider() {}
	
	public static FilmeBuider umFilme() {
		FilmeBuider buider = new FilmeBuider();
		buider.filme = new Filme();
		buider.filme.setEstoque(2);
		buider.filme.setNome("FIlme 1");
		buider.filme.setPrecoLocacao(4.0);
		return buider;
	}
	
	public static FilmeBuider umFilmeSemEstoque() {
		FilmeBuider buider = new FilmeBuider();
		buider.filme = new Filme();
		buider.filme.setEstoque(0);
		buider.filme.setNome("FIlme 1");
		buider.filme.setPrecoLocacao(4.0);
		return buider;
	}
	
	public FilmeBuider comValor(Double valor) {
		filme.setPrecoLocacao(valor);
		return this;
	}
	
	public FilmeBuider semEstoque() {
		filme.setEstoque(0);
		return this;
	}
	
	public Filme agora() {
		return filme;
	}

}
