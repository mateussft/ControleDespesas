package br.inf.safetech.cd.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import br.inf.safetech.cd.dao.UsuarioDAO;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private UsuarioDAO usuarioDao;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
//		.antMatchers("/resources/**").permitAll()
			.antMatchers("/contas").hasAnyRole("USER,ADMIN")
			.antMatchers("/contas/filtro").hasAnyRole("USER,ADMIN")
			.antMatchers("/contas/admin/**").hasRole("ADMIN")
			
			.antMatchers("/movimentacoes").hasAnyRole("USER,ADMIN")
			.antMatchers("/movimentacoes/detalhe").hasAnyRole("USER,ADMIN")
			.antMatchers("/movimentacoes/editar").hasAnyRole("USER,ADMIN")
			.antMatchers("/movimentacoes/formulario").hasAnyRole("USER,ADMIN")
			.antMatchers("/movimentacoes/deletar").hasAnyRole("USER,ADMIN")
			.antMatchers("/movimentacoes/financeiro/**").hasRole("ADMIN")
					
			.antMatchers("/usuarios").hasRole("ADMIN")
			.antMatchers("/usuarios/alterarSenha").hasAnyRole("USER,ADMIN")
			.antMatchers("/usuarios/alterarSenhaForm").hasAnyRole("USER,ADMIN")
			.antMatchers("/usuarios/**").hasRole("ADMIN")
			
			.antMatchers("/").permitAll()
			.antMatchers("/**").permitAll()
			.antMatchers("/popularBanco").permitAll()
			.anyRequest().authenticated().and().formLogin().loginPage("/login")
			.defaultSuccessUrl("/")
			.failureUrl("/login?error=true")
			.permitAll().and().logout()
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll()
			.logoutSuccessUrl("/login");

	http.csrf().disable();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(usuarioDao).passwordEncoder(new BCryptPasswordEncoder());
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**");
	}

}
