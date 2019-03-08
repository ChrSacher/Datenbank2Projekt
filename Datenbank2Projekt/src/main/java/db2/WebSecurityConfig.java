package db2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

@Configuration
// @EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	public void configure(WebSecurity web) throws Exception
	{
		web.ignoring().antMatchers("/css/**","/h2-console/**","/console/**","/resources/**");
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception
	{

		http.antMatcher("/**").authorizeRequests().anyRequest().permitAll();
//		// http.csrf().disable();
//		http.authorizeRequests()
//				.antMatchers("/login","/css/**","/customer/css/**","/buyPizzas","/register","/users/create","/cart","/addToCart","/removeFromCart","/clearCart","/adress","/deliveryAdress/create","/resources/**")
//				.permitAll().anyRequest().authenticated().and().formLogin().loginPage("/login")
//				.loginProcessingUrl("/logUserIn").defaultSuccessUrl("/", false).failureUrl("/login?error")
//				.usernameParameter("Login").passwordParameter("Password").permitAll().and().logout()
//				.logoutSuccessUrl("/").invalidateHttpSession(true).deleteCookies("JSESSIONID")
//				// .logoutUrl("/logout")
//				.permitAll().and().rememberMe();
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception
	{
		auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
	}
}
