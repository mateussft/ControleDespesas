package br.inf.safetech.cd.controllers;

import java.security.Principal;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import br.inf.safetech.cd.dao.ClienteDAO;
import br.inf.safetech.cd.dao.ContaDespesaDAO;
import br.inf.safetech.cd.dao.MovimentacaoContaDAO;
import br.inf.safetech.cd.dao.RoleDAO;
import br.inf.safetech.cd.dao.UsuarioDAO;
import br.inf.safetech.cd.models.Cliente;
import br.inf.safetech.cd.models.Role;
import br.inf.safetech.cd.models.Situacao;
import br.inf.safetech.cd.models.Usuario;

@Controller
public class HomeController {

	@Autowired
	private UsuarioDAO usuarioDAO;

	@Autowired
	private ClienteDAO clienteDAO;

	@Autowired
	private ContaDespesaDAO contaDespesaDAO;

	@Autowired
	private MovimentacaoContaDAO movimentacaoContaDAO;

	@Autowired
	private RoleDAO roleDAO;

	@RequestMapping("/")
	public ModelAndView index() {
		return new ModelAndView("redirect:/contas");
	}

	@Transactional
	@ResponseBody
	@RequestMapping("/popularBanco")
	public String urlMagicaMaluca() {

		Role roleAdmin = new Role("ROLE_ADMIN");
		Role roleUser = new Role("ROLE_USER");

		roleDAO.gravar(roleAdmin);
		roleDAO.gravar(roleUser);

		Cliente cliente1 = new Cliente("KLIN");
		Cliente cliente2 = new Cliente("BIBI");
		Cliente cliente3 = new Cliente("PAQUETÁ");
		Cliente cliente4 = new Cliente("COOPER");

		clienteDAO.gravar(cliente1);
		clienteDAO.gravar(cliente2);
		clienteDAO.gravar(cliente3);
		clienteDAO.gravar(cliente4);

		Usuario usuario = new Usuario();
		usuario.setNome("Admin");
		usuario.setLogin("admin@safetech.inf.br");
		usuario.setSenha("123456");
		usuario.setSituacao(Situacao.ATIVA);
		usuario.setRoles(Arrays.asList(roleAdmin));

		usuarioDAO.gravar(usuario);

		return "Pronto para teste/demonstração!";
	}

	@RequestMapping("/teste")
	public ModelAndView teste(Principal principal, User user) {
		return new ModelAndView("redirect:/contas");
	}
}