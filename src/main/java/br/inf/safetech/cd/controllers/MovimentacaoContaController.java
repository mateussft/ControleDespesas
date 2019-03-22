package br.inf.safetech.cd.controllers;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.inf.safetech.cd.dao.ContaDespesaDAO;
import br.inf.safetech.cd.dao.MovimentacaoContaDAO;
import br.inf.safetech.cd.models.Conciliada;
import br.inf.safetech.cd.models.ContaDespesa;
import br.inf.safetech.cd.models.MovimentacaoConta;
import br.inf.safetech.cd.models.Responsavel;
import br.inf.safetech.cd.models.Situacao;
import br.inf.safetech.cd.models.Tipo;
import br.inf.safetech.cd.models.Usuario;

@RequestMapping("/movimentacoes")
@Controller
public class MovimentacaoContaController {

	@Autowired
	private ContaDespesaDAO contaDespesaDAO;

	@Autowired
	private MovimentacaoContaDAO movimentacaoContaDAO;

	// lista as movimentações na conta
	@RequestMapping(value = "/detalhe", method = RequestMethod.GET)
	public ModelAndView listar(Authentication auth, Principal principal, @RequestParam("id") String id,
			RedirectAttributes redirectAttributes) {
		List<MovimentacaoConta> movimentacoes = movimentacaoContaDAO.listarId(Integer.parseInt(id));
		BigDecimal saldoLiquido = contaDespesaDAO.calculaSaldoLiquido(Integer.parseInt(id));
		BigDecimal saldo = contaDespesaDAO.calculaSaldo(Integer.parseInt(id));
		BigDecimal credito = contaDespesaDAO.getCredito(Integer.parseInt(id));
		BigDecimal debito = contaDespesaDAO.getDebito(Integer.parseInt(id));
		String colaborador = movimentacoes.get(0).getConta().getUsuario().getLogin();
		String usuarioLogado = principal.getName();
		if (!hasRole(auth, "ROLE_ADMIN")) {
			if (!colaborador.equals(usuarioLogado)) {
				redirectAttributes.addFlashAttribute("message",
						"Não é possivel vizualizar movimentações de outros colaboradores!");
				return new ModelAndView("redirect:/contas");
			}
		} // fim do if
		ModelAndView modelAndView = new ModelAndView("movimentacoes/lista");
		modelAndView.addObject("movimentacoes", movimentacoes);
		modelAndView.addObject("saldo", saldo);
		modelAndView.addObject("saldoLiquido", saldoLiquido);
		modelAndView.addObject("credito", credito);
		modelAndView.addObject("debito", debito);
		return modelAndView;
	}

	@RequestMapping(value = "/editar", method = RequestMethod.GET)
	public ModelAndView editar(Authentication auth, Principal principal, RedirectAttributes redirectAttributes,
			@RequestParam("id") String id) {
		//id = id.substring(1);
		List<MovimentacaoConta> movimentacoes = movimentacaoContaDAO.listarPorId(Integer.parseInt(id));
		BigDecimal saldoLiquido = contaDespesaDAO.calculaSaldoLiquido(Integer.parseInt(id));
		BigDecimal saldo = contaDespesaDAO.calculaSaldo(Integer.parseInt(id));
		BigDecimal credito = contaDespesaDAO.getCredito(Integer.parseInt(id));
		BigDecimal debito = contaDespesaDAO.getDebito(Integer.parseInt(id));

		if(movimentacoes.size() == 0) {
			redirectAttributes.addFlashAttribute("message", "Essa conta não possui movimentações!");
			return new ModelAndView("redirect:/contas");
		}

		int colaborador = movimentacoes.get(0).getConta().getUsuario().getId();
		Usuario usuarioLogado = (Usuario) ((Authentication) principal).getPrincipal();
		
		Situacao situacao = movimentacoes.get(0).getConta().getSituacao();

		if (!hasRole(auth, "ROLE_ADMIN")) {
			if (colaborador != usuarioLogado.getId()) {
				redirectAttributes.addFlashAttribute("message", "ERRO! Você só pode editar as contas que lhe pertencem!");
				return new ModelAndView("redirect:/contas");
			} else if (situacao.equals(Situacao.ENCERRADA)) {
				redirectAttributes.addFlashAttribute("message", "ERRO! A conta está encerrada !");
				return new ModelAndView("redirect:/contas");
			}
		} else {
			if (situacao.equals(Situacao.ENCERRADA)) {
				redirectAttributes.addFlashAttribute("message", "ERRO! A conta está encerrada !");
				return new ModelAndView("redirect:/contas");
			}
		}

		List<Responsavel> responsaveis = new ArrayList<>();
		responsaveis.add(Responsavel.EMPRESA);
		responsaveis.add(Responsavel.COLABORADOR);
		responsaveis.add(Responsavel.CLIENTE);
		
		ModelAndView modelAndView = new ModelAndView("movimentacoes/editar");
		modelAndView.addObject("movimentacoes", movimentacoes);
		modelAndView.addObject("responsaveis", responsaveis);
		modelAndView.addObject("saldo", saldo);
		modelAndView.addObject("saldoLiquido", saldoLiquido);
		modelAndView.addObject("credito", credito);
		modelAndView.addObject("debito", debito);
		return modelAndView;
	}

	@RequestMapping(value = "/formulario", method = RequestMethod.POST)
	public ModelAndView formulario(MovimentacaoConta movimentacaoConta, @RequestParam("id") String id) {
		id = id.substring(1);
		ContaDespesa conta = contaDespesaDAO.find(Integer.parseInt(id));
		movimentacaoConta.setConta(conta);
		ModelAndView modelAndView = new ModelAndView("movimentacoes/form");
		modelAndView.addObject("conta", conta);
		return modelAndView;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView gravar(MovimentacaoConta movimentacaoConta, Principal principal,
			RedirectAttributes redirectAttributes) {
		if(movimentacaoConta.getTipo()== Tipo.CREDITO) {
			movimentacaoConta.setConciliada(Conciliada.SIM);
			movimentacaoConta.setResponsavel(Responsavel.EMPRESA);
		}
		ContaDespesa cd = contaDespesaDAO.find(movimentacaoConta.getConta().getId());
		Usuario usuarioLogado = (Usuario) ((Authentication) principal).getPrincipal();
		movimentacaoConta.setConta(cd);
		movimentacaoConta.setUsuario(usuarioLogado);

		movimentacaoContaDAO.gravar(movimentacaoConta);
		redirectAttributes.addFlashAttribute("message", "Movimentação cadastrada com sucesso!");
		return new ModelAndView("redirect:/contas");
	}

	@RequestMapping(value = "/financeiro/conciliar", method = RequestMethod.POST)
	public ModelAndView conciliar(Authentication auth, RedirectAttributes redirectAttributes,
			@RequestParam("id") String id, @RequestParam("conta") String contaId, @RequestParam("tipo") String tipo) {
		id = id.substring(1);
		tipo = tipo.substring(1);
		contaId = contaId.substring(1);
		if (hasRole(auth, "ROLE_ADMIN")) {
			if (tipo.equals("SIM")) {
				movimentacaoContaDAO.conciliar(Integer.parseInt(id));
			} else {
				movimentacaoContaDAO.desconciliar(Integer.parseInt(id));
			}
			redirectAttributes.addFlashAttribute("message", "Conciliação atualizada com sucesso!");
			return new ModelAndView("redirect:/movimentacoes/editar?id=" + contaId);
		} else {
			redirectAttributes.addFlashAttribute("message", "Erro: Tarefa para administradores");
			return new ModelAndView("redirect:/contas");
		}
	}

	@RequestMapping(value = "/financeiro/atualizar", method = RequestMethod.POST)
	public ModelAndView atualizar(RedirectAttributes redirectAttributes, @RequestParam("id") String id, @RequestParam("conta") String contaId,
			@RequestParam("responsavel") String responsavel) {
		ModelAndView modelAndView = new ModelAndView("movimentacoes/editar");
		id = id.substring(1);
		contaId = contaId.substring(1);
		responsavel = responsavel.substring(1);

		Responsavel res = Responsavel.valueOf(responsavel);

		movimentacaoContaDAO.alterarResponsavel(Integer.parseInt(id), res);
		
		redirectAttributes.addFlashAttribute("message", "Responsável alterado com sucesso!");
		return new ModelAndView("redirect:/movimentacoes/editar?id="+contaId);
	}

	@RequestMapping(value = "/deletar", method = RequestMethod.POST)
	public ModelAndView remover(@RequestParam("id") String id, @RequestParam("conta") String contaId,
			RedirectAttributes redirectAttributes) {
		ModelAndView modelAndView = new ModelAndView("movimentacoes/editar");
		id = id.substring(1);
		contaId = contaId.substring(1);
		movimentacaoContaDAO.remover(Integer.parseInt(id));
		redirectAttributes.addFlashAttribute("message", "Removido com sucesso");
		return new ModelAndView("redirect:/movimentacoes/editar?id=" + contaId);
	}

	private boolean hasRole(Authentication auth, String role) {
		return auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals(role));
	}
}