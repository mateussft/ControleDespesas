package br.inf.safetech.cd.controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.inf.safetech.cd.dao.ClienteDAO;
import br.inf.safetech.cd.dao.ContaDespesaDAO;
import br.inf.safetech.cd.dao.MovimentacaoContaDAO;
import br.inf.safetech.cd.dao.UsuarioDAO;
import br.inf.safetech.cd.models.Cliente;
import br.inf.safetech.cd.models.Conciliada;
import br.inf.safetech.cd.models.ContaDespesa;
import br.inf.safetech.cd.models.MovimentacaoConta;
import br.inf.safetech.cd.models.Responsavel;
import br.inf.safetech.cd.models.Situacao;
import br.inf.safetech.cd.models.Tipo;
import br.inf.safetech.cd.models.Usuario;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@RequestMapping("/contas")
@Controller
public class ContaDespesaController {

	@Autowired
	private ClienteDAO clienteDAO;

	@Autowired
	private ContaDespesaDAO contaDespesaDAO;
	
	@Autowired
	private MovimentacaoContaDAO movimentacaoContaDAO;


	@Autowired
	private UsuarioDAO usuarioDAO;

	@InitBinder
	public void initBinder(WebDataBinder webDataBinder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		dateFormat.setLenient(false);
		webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

//mostra as contas existentes para os administradores
	@RequestMapping(value = "/todasContas", method = RequestMethod.GET)
	public ModelAndView listarContas() {
		ModelAndView modelAndView = new ModelAndView("home");
		List<ContaDespesa> contas = contaDespesaDAO.listar();
		List<Usuario> usuarios = usuarioDAO.listar();
		List<Cliente> clientes = clienteDAO.listar();

		modelAndView.addObject("usuarios", usuarios);
		modelAndView.addObject("clientes", clientes);
		modelAndView.addObject("contas", contas);
		return modelAndView;
	}

//método de listagem de contas para o user
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView listar(Principal principal, Authentication auth) {
		ModelAndView modelAndView = new ModelAndView("home");
		List<ContaDespesa> contas;
		List<Usuario> usuarios = usuarioDAO.listar();
		List<Cliente> clientes = clienteDAO.listar();
		
		boolean roleUser = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_USER"));
		if (roleUser) {
			Usuario usuario = usuarioDAO.loadUserByUsername(principal.getName());
			contas = contaDespesaDAO.contaColaborador(usuario);
			if (contas.size() == 0)
				modelAndView.addObject("message", "Não ha contas");
		} else {
			contas = contaDespesaDAO.listar();
		}
		
		Map<Integer, BigDecimal> saldos = new HashMap<Integer, BigDecimal>();
		for (ContaDespesa conta : contas) {
			BigDecimal saldo = contaDespesaDAO.calculaSaldo(conta.getId());
			saldos.put(conta.getId(), saldo);
		}
		
		modelAndView.addObject("usuarios", usuarios);
		modelAndView.addObject("clientes", clientes);
		modelAndView.addObject("contas", contas);
		modelAndView.addObject("saldos", saldos);
		return modelAndView;
	}

//método para cadastro de conta a partir do formulário
	@RequestMapping(value = "/adm/formulario", method = RequestMethod.GET)
	public ModelAndView formulario(ContaDespesa contaDespesa) {
		ModelAndView modelAndView = new ModelAndView("conta/formulario");
		List<Cliente> clientes = clienteDAO.listar();
		modelAndView.addObject("clientes", clientes);
		List<Usuario> usuarios = usuarioDAO.listar();
		modelAndView.addObject("usuarios", usuarios);
		return modelAndView;
	}

//método que grava a conta com os clientes, colaboradores, etc
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView gravar(ContaDespesa conta, RedirectAttributes redirectAttributes) throws ParseException {
		// usando a classe Date para ter a data do sistema na criação da conta
		Date date = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Date dt = sf.parse(sf.format(date));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dt);
		
		
		Cliente cliete = clienteDAO.find(conta.getCliente().getId());
		Usuario colaborador = usuarioDAO.find(conta.getUsuario().getId());
		conta.setCliente(cliete);
		conta.setUsuario(colaborador);
		conta.setDataInicio(calendar);
		// já cadastra por padrão a conta como ativa
		conta.setSituacao(Situacao.ATIVA);
		contaDespesaDAO.gravar(conta);
		// redireciona de volta com a mensagem de sucesso
		redirectAttributes.addFlashAttribute("message", "Conta cadastrada com sucesso!");
		return new ModelAndView("redirect:/contas");
	}

//método de fechamento de conta
	@RequestMapping(value = "/adm/encerrar/form", method = RequestMethod.POST)
	public ModelAndView encerrarConta(@RequestParam("id") String id, RedirectAttributes redirectAttributes)
			throws NumberFormatException, ParseException {
		ModelAndView modelAndView = new ModelAndView("conta/encerrar");
		id = id.substring(1);
		ContaDespesa conta = contaDespesaDAO.find(Integer.parseInt(id));
		BigDecimal saldo = contaDespesaDAO.calculaSaldo(Integer.parseInt(id));

		if (contaDespesaDAO.estaLiquidada(Integer.parseInt(id))) {
				contaDespesaDAO.encerrar(Integer.parseInt(id));
				redirectAttributes.addFlashAttribute("message", "Conta encerrada com sucesso!");
				return new ModelAndView("redirect:/contas");
		} 
		else {
			redirectAttributes.addFlashAttribute("message",
					"Não é possível encerrar esta conta"
					+ " <br/> Verifique se ela tem responsáveis e esta com status de conciliada.");
			return new ModelAndView("redirect:/contas");
		}

	}

	
	
	/*@RequestMapping(value = "/encerrar", method = RequestMethod.POST)
	public ModelAndView encerrarConta(@RequestParam("id") String id, RedirectAttributes redirectAttributes)
			throws ParseException {
		id = id.substring(1);
		contaDespesaDAO.encerrar(Integer.parseInt(id));
		redirectAttributes.addFlashAttribute("message", "Conta encerrada com sucesso!");
		return new ModelAndView("redirect:/contas");
	}*/

	@RequestMapping(value = "/adm/vale", method = RequestMethod.POST)
	public ModelAndView vale(Principal principal, @RequestParam("id") String id, RedirectAttributes redirectAttributes)
			throws ParseException {
		id = id.substring(1);
		
		MovimentacaoConta m = new MovimentacaoConta();
		ContaDespesa c = contaDespesaDAO.find(Integer.parseInt(id));
		Usuario u = (Usuario) ((Authentication) principal).getPrincipal();
		Usuario usuarioLogado = (Usuario) ((Authentication) principal).getPrincipal();

		m.setUsuario(usuarioLogado);
		m.setConta(c);
		m.setTipo(Tipo.DEBITO);
		m.setConciliada(Conciliada.SIM);
		m.setResponsavel(Responsavel.COLABORADOR);
		m.setDescricao("Devolução em forma de vale");
		m.setValor(contaDespesaDAO.calculaSaldo(c.getId()));
				
		movimentacaoContaDAO.gravar(m);
		contaDespesaDAO.encerrar(Integer.parseInt(id));
		contaDespesaDAO.vale(Integer.parseInt(id));
		redirectAttributes.addFlashAttribute("message", "Escolha cadastrada com sucesso!");
		return new ModelAndView("redirect:/contas");
	}

	@RequestMapping(value = "/adm/devolucao", method = RequestMethod.POST)
	public ModelAndView devolucao(Principal principal, @RequestParam("id") String id, RedirectAttributes redirectAttributes)
			throws ParseException {
		id = id.substring(1);
		
		MovimentacaoConta m = new MovimentacaoConta();
		ContaDespesa c = contaDespesaDAO.find(Integer.parseInt(id));
		Usuario u = (Usuario) ((Authentication) principal).getPrincipal();
		Usuario usuarioLogado = (Usuario) ((Authentication) principal).getPrincipal();
		m.setUsuario(usuarioLogado);
		m.setConta(c);
		m.setTipo(Tipo.DEBITO);
		m.setConciliada(Conciliada.SIM);
		m.setResponsavel(Responsavel.EMPRESA);
		m.setDescricao("Devolução do restante para a empresa");
		m.setValor(contaDespesaDAO.calculaSaldo(c.getId()));
		movimentacaoContaDAO.gravar(m);
		contaDespesaDAO.encerrar(Integer.parseInt(id));
		contaDespesaDAO.devolver(Integer.parseInt(id));
		redirectAttributes.addFlashAttribute("message", "Escolha cadastrada com sucesso!");
		return new ModelAndView("redirect:/contas");
	}
	@RequestMapping(value = "/filtro", method = RequestMethod.GET)
	public ModelAndView filtrar(Principal principal, Authentication auth,
			@RequestParam("usuario") Optional<String> usuario, @RequestParam("cliente") Optional<String> cliente,
			@RequestParam("dataInicio") Optional<String> dataInicio,
			@RequestParam("dataFinal") Optional<String> dataFinal, @RequestParam("situacao") Optional<String> situacao,
			RedirectAttributes redirectAttributes) throws ParseException {

		ModelAndView modelAndView = new ModelAndView("home");
		
		String colaborador = (usuario.isPresent()) ? (colaborador = usuario.get()) : (colaborador = "");
		String Cliente = (cliente.isPresent()) ? (Cliente = cliente.get()) : (Cliente = "");
		String dataInicial = (dataInicio.isPresent()) ? (dataInicial = dataInicio.get()) : (dataInicial = "");
		String dataFim = (dataFinal.isPresent()) ? (dataFim = dataFinal.get()) : (dataFim = "");
		String varSituacao = (situacao.isPresent()) ? (varSituacao = situacao.get()) : (varSituacao = "");

		Situacao sit = Situacao.valueOf(varSituacao);
		// inicializa as variaveis como nulas
		Calendar cal_dataInicio = null;
		Calendar cal_dataFinal = null;

		if (dataInicial.length() > 0) {
			// verifica o formato de data
			if (!dataInicial.matches("\\d{2}/\\d{2}/\\d{4}")) {
				redirectAttributes.addFlashAttribute("message", "Insira a data no formato dia/mês/ano!");
				return new ModelAndView("redirect:/contas");
			} else {
				cal_dataInicio = StringToDate(dataInicial);
			}
		}

		if (dataFim.length() > 0) {
			// verifica o formato de data
			if (!dataFim.matches("\\d{2}/\\d{2}/\\d{4}")) {
				redirectAttributes.addFlashAttribute("message", "Insira a data no formato dia/mês/ano!");
				return new ModelAndView("redirect:/contas");
			} else {
				cal_dataFinal = StringToDate(dataFim);
			}
		}

		List<ContaDespesa> contas;
		boolean roleUser = auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_USER"));
		// verifica o tipo de usuario para o retorno certo
		if (roleUser) {
			Usuario user = usuarioDAO.loadUserByUsername(principal.getName());
			contas = contaDespesaDAO.filtro(user.getNome(), Cliente, cal_dataInicio, cal_dataFinal, sit);
			if (contas.size() == 0)
				modelAndView.addObject("message", "Conta não localizada no banco de dados");
		} else {
			contas = contaDespesaDAO.filtro(colaborador, Cliente, cal_dataInicio, cal_dataFinal, sit);
		}

		List<Usuario> usuarios = usuarioDAO.listar();
		List<Cliente> clientes = clienteDAO.listar();
		modelAndView.addObject("message", "Resultados: ");
		modelAndView.addObject("usuarios", usuarios);
		modelAndView.addObject("clientes", clientes);
		modelAndView.addObject("contas", contas);

		return modelAndView;
	}

	// Transforma a string em calendar, tornando possível a busca no banco
	private Calendar StringToDate(String data) throws ParseException {
		String[] datas = data.split("/");
		data = datas[2] + "-" + datas[1] + "-" + datas[0];

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Date date = sdf.parse(data);
		Calendar cal_data = Calendar.getInstance();
		cal_data.setTime(date);

		return cal_data;
	}

	
	@RequestMapping(value = "/admin/gerarRelatorio", method = RequestMethod.POST)
	public ModelAndView gerarRelatorio(@RequestParam("pdfcliente") Optional<String> pdfcliente, Principal principal, HttpServletRequest request, HttpServletResponse response,
			@RequestParam("conta") String conta, RedirectAttributes redirectAttributes)
			throws ParseException, JRException, IOException {
		conta = conta.substring(1);

		List<MovimentacaoConta> movimentacoes;
		if (pdfcliente.isPresent() && pdfcliente.get() != "") {
			movimentacoes = movimentacaoContaDAO.listarDoClientePorId(Integer.parseInt(conta));
			if(movimentacoes.size() == 0) {
				redirectAttributes.addFlashAttribute("message", "O cliente não é responsável por nenhuma movimentação!");
				return new ModelAndView("redirect:/movimentacoes/detalhe?id="+conta);
			}
		} else {
			movimentacoes = movimentacaoContaDAO.listarPorId(Integer.parseInt(conta));
		}

		ContaDespesa contaDespesa = movimentacoes.get(0).getConta();

		Usuario usuarioLogado = (Usuario) ((Authentication) principal).getPrincipal();

		String RelatorioCriadoPor = usuarioLogado.getNome();
		String colaborador = contaDespesa.getUsuario().getNome();
		String cliente = contaDespesa.getCliente().getNome();
		String situacaoConta = contaDespesa.getSituacao().name();

		Date dataInicio = contaDespesa.getDataInicio().getTime();
		Date dataFim = contaDespesa.getDataFim() != null ? contaDespesa.getDataFim().getTime() : null;

		BigDecimal Credito = contaDespesaDAO.calculaCreditoMovimentacoes(movimentacoes);
		BigDecimal Debito = contaDespesaDAO.calculaDebitoMovimentacoes(movimentacoes);
		BigDecimal Saldo = contaDespesaDAO.calculaSaldoMovimentacoes(movimentacoes);

		List<Map<String, ?>> datasource = new ArrayList<Map<String, ?>>();

		Map<String, Object> teste = new HashMap<String, Object>();
		// Outras variaveis
		teste.put("criadorPdf", RelatorioCriadoPor);
		teste.put("criadorConta", contaDespesa.getUsuario().getNome());
		teste.put("situacao", situacaoConta.substring(0, 1).toUpperCase() + situacaoConta.substring(1).toLowerCase());
		teste.put("colaborador", colaborador);
		teste.put("cliente", cliente);
		teste.put("dataInicio", dataInicio);
		teste.put("dataFim", dataFim);
		teste.put("credito", Credito);
		teste.put("debito", Debito);
		teste.put("saldo", Saldo);
		datasource.add(teste);

		for (MovimentacaoConta mov : movimentacoes) {

			Map<String, Object> m = new HashMap<String, Object>();
			String responsavel = mov.getResponsavel() != null
					? mov.getResponsavel().name().substring(0, 1).toUpperCase()
							+ mov.getResponsavel().name().substring(1).toLowerCase()
					: "";

			m.put("tipo", mov.getTipo().name().substring(0, 1).toUpperCase()
					+ mov.getTipo().name().substring(1).toLowerCase());
			m.put("responsavel", responsavel);
			m.put("conciliada", mov.getConciliada().name().substring(0, 1).toUpperCase()
					+ mov.getConciliada().name().substring(1).toLowerCase());
			m.put("valor", mov.getValor());
			m.put("descricao", mov.getDescricao());
			m.put("criadoPor", mov.getUsuario().getNome());

			datasource.add(m);
		}

		JRBeanCollectionDataSource jrDataSource = new JRBeanCollectionDataSource(datasource);

		String nome = request.getServletContext().getRealPath("/relatorio/relatorio.jasper");

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ItemDataSource", jrDataSource);

		JasperPrint jasperPrint = JasperFillManager.fillReport(nome, parameters, jrDataSource);

		String filename = "Relatorio " + colaborador + "-" + cliente + ".pdf";
		response.setContentType("application/x-pdf");
		response.setHeader("Content-disposition", "inline; filename=" + filename);
		
		OutputStream outStream = response.getOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
		
		outStream.flush();
		outStream.close();
		
		return new ModelAndView("redirect:/movimentacoes/detalhe?id="+conta);
	}
	
	
	
	
	
	
	
	
}