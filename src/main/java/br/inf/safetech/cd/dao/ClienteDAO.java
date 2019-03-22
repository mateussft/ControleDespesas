package br.inf.safetech.cd.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.inf.safetech.cd.models.Cliente;

@Repository
@Transactional
public class ClienteDAO {

	@PersistenceContext
	private EntityManager manager;

	public Cliente find(Integer id) {
		return manager.find(Cliente.class, id);
	}

//busca todos clientes sem criterio de busca
	public List<Cliente> listar() {
		return manager.createQuery("select c from Cliente c", Cliente.class).getResultList();
	}

	public void gravar(Cliente cliente) {
		System.out.println("gravando cliente");
		manager.persist(cliente);
	}

}