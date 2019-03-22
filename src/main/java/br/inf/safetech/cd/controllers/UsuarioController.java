package br.inf.safetech.cd.controllers;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.inf.safetech.cd.dao.UsuarioDAO;
import br.inf.safetech.cd.models.Usuario;

@RequestMapping("/usuarios")
@Controller
public class UsuarioController {

	@Autowired
	private UsuarioDAO usuarioDao;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView listar() {
		List<Usuario> usuarios = usuarioDao.listar();
		ModelAndView modelAndView = new ModelAndView("usuarios/lista");
		modelAndView.addObject("usuarios", usuarios);
		return modelAndView;
	}

	@RequestMapping(value = "/formulario", method = RequestMethod.GET)
	public ModelAndView formulario(Usuario usuario) {
		List<Usuario> usuarios = usuarioDao.listar();
		ModelAndView modelAndView = new ModelAndView("usuarios/formulario");
		modelAndView.addObject("usuarios", usuarios);
		return modelAndView;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView gravar(@Valid Usuario usuario, BindingResult result, RedirectAttributes redirectAttributes) {
		if (usuarioDao.usuarioJaExiste(usuario)) {
			redirectAttributes.addFlashAttribute("message", "Erro! Colaborador já existe no sistema");
			return new ModelAndView("redirect:/usuarios/form");
		}
		usuarioDao.gravar(usuario);
		redirectAttributes.addFlashAttribute("message", "Novo colaborador cadastrado!");
		return new ModelAndView("redirect:/contas");
	}

	@RequestMapping(value = "/alterarSenhaForm", method = RequestMethod.GET)
	public ModelAndView formSenha(Principal principal) {

		Usuario usuarioLogado = (Usuario) ((Authentication) principal).getPrincipal();
		usuarioLogado.setSenha("");

		ModelAndView modelAndView = new ModelAndView("usuarios/formSenha");
		modelAndView.addObject("usuario", usuarioLogado);
		return modelAndView;
	}

	@RequestMapping(value = "/alterarSenha", method = RequestMethod.POST)
	public ModelAndView alterarSenha(Usuario usuario, RedirectAttributes redirectAttributes,
			BindingResult result, Principal principal) {
		if(!usuario.getSenha().equals(usuario.getSenhaRepetida())) {
			redirectAttributes.addFlashAttribute("message", "Informe senhas iguais");
			return new ModelAndView("redirect:/usuarios/alterarSenhaForm");
		}
		usuarioDao.alterarSenha(usuario.getId(), usuario.getSenha());
		redirectAttributes.addFlashAttribute("message", "Sua senha foi atualizada");
		return new ModelAndView("redirect:/contas");
	}
}