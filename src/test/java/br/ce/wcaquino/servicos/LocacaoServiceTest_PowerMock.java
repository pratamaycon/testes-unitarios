package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.buiders.FilmeBuider.umFilme;
import static br.ce.wcaquino.buiders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.marchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.marchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.marchers.MatchersProprios.ehHojeComDiferencasDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.utils.DataUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LocacaoService.class, DataUtils.class})
public class LocacaoServiceTest_PowerMock {
	
	@InjectMocks
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
		service = PowerMockito.spy(service);
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		// assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// Cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().comValor(5.0).agora());
		
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(5, 02, 2021));
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(locacao.getDataLocacao(), ehHoje());
		error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencasDias(1));
		//error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), DataUtils.obterData(5, 2, 2021)), is(true));
		//error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterData(6, 2, 2021)), is(true));
	}
	

	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws Exception {
		//assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(DataUtils.obterData(6, 02, 2021));
		
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		//verificao
		//assertThat(retorno.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
		//assertThat(retorno.getDataRetorno(), caiEm(Calendar.SUNDAY));
		assertThat(retorno.getDataRetorno(), caiNumaSegunda());
		//boolean ehSegunda = DataUtils.verificarDiaSemana(retorno.getDataRetorno(), Calendar.MONDAY);
		//assertTrue(ehSegunda);
		
		PowerMockito.verifyNew(Date.class, Mockito.times(2)).withNoArguments();
		
	}
	
	@Test
	public void deveAlugarFilme_SemCalcularValor() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		PowerMockito.doReturn(1.0).when(service, "calcularValorLocacao", filmes);
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificao
		assertThat(locacao.getValor(), is(1.0));
		PowerMockito.verifyPrivate(service).invoke("calcularValorLocacao", filmes);
	}
	
	@Test
	public void deveCalcularValorLocacao() throws Exception {
		//cenario
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		//acao
		Double valor = (Double) Whitebox.invokeMethod(service, "calcularValorLocacao", filmes);
		
		//verificao
		assertThat(valor, is(4.0));
	}


}	
