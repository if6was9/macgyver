package io.macgyver.core.auth;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class InternalAuthenticationProvider implements AuthenticationProvider {

	org.slf4j.Logger logger = LoggerFactory
			.getLogger(InternalAuthenticationProvider.class);

	@Autowired
	UserManager userManager;

	@Autowired
	AccessDecisionManager adm;

	GrantedAuthoritiesMapper grantedAuthoritiesMapper;
	
	public void setAuthoritiesMapper(GrantedAuthoritiesMapper mapper) {
		this.grantedAuthoritiesMapper = mapper;
	}
	
	@Override
	public Authentication authenticate(final Authentication authentication)
			throws AuthenticationException {
		
		Optional<JsonObject> u = userManager.getUserAsJsonObject(authentication
				.getPrincipal().toString());
		if (!u.isPresent()) {
			throw new UsernameNotFoundException("user not found: "+authentication.getPrincipal().toString());
		}
		boolean b = userManager.authenticate(authentication.getPrincipal()
				.toString(), authentication.getCredentials().toString());
		if (!b) {
			throw new BadCredentialsException("invalid credentials");
		}
		
		JsonElement r = u.get().get("roles");

		List<GrantedAuthority> gaList = Lists.newArrayList();
		if (r != null && r.isJsonArray()) {
			JsonArray arr = r.getAsJsonArray();
			for (int i = 0; i < arr.size(); i++) {
				String role = arr.get(i).getAsString();
				GrantedAuthority ga = new SimpleGrantedAuthority(role);
				gaList.add(ga);
			}
		}

		UsernamePasswordAuthenticationToken upt = new UsernamePasswordAuthenticationToken(
				authentication.getPrincipal().toString(), authentication
						.getCredentials().toString(), gaList);
		return upt;

	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class
				.isAssignableFrom(authentication);

	}

	public void seedData() {

		Optional<JsonObject> adminUser = userManager
				.getUserAsJsonObject("admin");
		if (!adminUser.isPresent()) {
			JsonObject x = new JsonObject();
			x.addProperty("username", "admin");
			JsonArray arr = new JsonArray();
			arr.add(new Gson().toJsonTree("ROLE_MACGYVER_SHELL"));
			arr.add(new Gson().toJsonTree("ROLE_MACGYVER_USER"));
			arr.add(new Gson().toJsonTree("ROLE_MACGYVER_ADMIN"));
			x.add("roles", arr);
			userManager.save(x);
			userManager.setPassword("admin", "admin");
			
		}
		
	}

}
