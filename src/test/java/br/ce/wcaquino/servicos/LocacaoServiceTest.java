package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.buiders.FilmeBuider.umFilme;
import static br.ce.wcaquino.buiders.FilmeBuider.umFilmeSemEstoque;
import static br.ce.wcaquino.buiders.LocacaoBuilder.umLocacao;
import static br.ce.wcaquino.buiders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.marchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.marchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.marchers.MatchersProprios.ehHojeComDiferencasDias;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	
	@InjectMocks
	@Spy
	private LocacaoService service;
	
	@Mock
	private LocacaoDAO dao;
	
	@Mock
	private SPCService spc;
	
	@Mock
	private EmailService email;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		// assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// Cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());
		
		Mockito.doReturn(DataUtils.obterData(5, 2, 2021)).when(service).obterData();
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(5, 2, 2021)), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(6, 2, 2021)), is(true));
	}
	
	// forma elegante
	
	@Test(expected = FilmeSemEstoqueException.class)
	public void deveLancarExcecaoAoAlugarFilmeSemEstoque() throws Exception  {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());
		
		//acao
		service.alugarFilme(usuario, filmes);
	}
	
	// forma robusta
	
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException  {
		//cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//acao
		try {
			service.alugarFilme(null, filmes);
			fail();
		} catch (LocadoraException e) {
			// TODO Auto-generated catch block
			assertThat(e.getMessage(), is("Usuario vazio"));
		}
	}
	
	// forma 'nova'
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = umUsuario().agora();
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
		
		//acao
		service.alugarFilme(usuario, null);
	}
	
	@Test
	public void devePagar75PctNoFilme3() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(11.0).agora());
		
		//acao
		Locacao resLocacao = service.alugarFilme(usuario, filmes);
		
		//verificao
		assertThat(resLocacao.getValor(), is(11.0));
	}
	
	@Test
	public void devePagar50PctNoFilme4() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(13.0).agora());
		
		//acao
		Locacao resLocacao = service.alugarFilme(usuario, filmes);
		
		//verificao
		assertThat(resLocacao.getValor(), is(13.0));
	}
	
	@Test
	public void devePagar25PctNoFilme5() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(14.0).agora());
		
		//acao
		Locacao resLocacao = service.alugarFilme(usuario, filmes);
		
		//verificao
		assertThat(resLocacao.getValor(), is(14.0));
	}
	
	@Test
	public void devePagar0PctNoFilme6() throws FilmeSemEstoqueException, LocadoraException {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(14.0).agora());
		
		//acao
		Locacao resLocacao = service.alugarFilme(usuario, filmes);
		
		//verificao
		assertThat(resLocacao.getValor(), is(14.0));
	}
	
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
		//assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		Mockito.doReturn(DataUtils.obterData(6, 2, 2021)).when(service).obterData();
		
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		//verificao
		//assertThat(retorno.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
		//assertThat(retorno.getDataRetorno(), caiEm(Calendar.SUNDAY));
		assertThat(retorno.getDataRetorno(), caiNumaSegunda());
		//boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
		//assertTrue(ehSegunda);
		
	}
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		when(spc.possuiNegativacao(any(Usuario.class))).thenReturn(true);
		
		//acao
		try {
			service.alugarFilme(usuario, filmes);
			//verificao
			fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario Negativado"));
		}
		
		verify(spc).possuiNegativacao(usuario);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas() {
		//cenario
		Usuario usuario = umUsuario().agora();
		Usuario usuario2 = umUsuario().comNome("Usuario em dia").agora();
		Usuario usuario3 = umUsuario().comNome("Outro atrasado").agora();
		List<Locacao> locacoes = Arrays.asList(
				umLocacao().atrasada().comUsuario(usuario).agora(),
				umLocacao().comUsuario(usuario2).agora(),
				umLocacao().atrasada().comUsuario(usuario3).agora());
		when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//acao
		service.notificarAtrasos();
		
		//verificao
		verify(email, times(2)).notiticarAtraso(any(Usuario.class));
		verify(email).notiticarAtraso(usuario);
		verify(email, atLeastOnce()).notiticarAtraso(usuario3);
		verify(email, never()).notiticarAtraso(usuario2);
		verifyNoMoreInteractions(email);
	}
	
	@Test
	public void deveTratarErroNoSPC() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		when(spc.possuiNegativacao(usuario)).thenThrow(new Exception("FALHA CASTRATRÓFICA"));
		
		//verificao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Problemas com SPC, tente novamente");
		
		//acao
		service.alugarFilme(usuario, filmes);
	}
	
	@Test
	public void deveProrrogarUmaLocacao() {
		//cenario
		Locacao locacao = umLocacao().agora();
		
		//acao
		service.prorrogarLocacao(locacao, 3);
		
		//verificao
		ArgumentCaptor<Locacao> captor = ArgumentCaptor.forClass(Locacao.class);
		verify(dao).salvar(captor.capture());
		Locacao locacaoRetornada = captor.getValue();
		
		error.checkThat(locacaoRetornada.getValor(), is(12.0));
		error.checkThat(locacaoRetornada.getDataLocacao(), ehHoje());
		error.checkThat(locacaoRetornada.getDataRetorno(), ehHojeComDiferencasDias(3));
		
	}
	
	@Test
	public void deveCalcularValorLocacao() throws Exception {
		//cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//acao
		Class<LocacaoService> clazz = LocacaoService.class;
		Method metodo = clazz.getDeclaredMethod("calcularValorLocacao", List.class);
		metodo.setAccessible(true);
		
		Double valor = (Double) metodo.invoke(service, filmes);
		
		//verificao
		assertThat(valor, is(4.0));
	}


}	
