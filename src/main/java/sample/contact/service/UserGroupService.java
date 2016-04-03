/**
 * 
 */
package sample.contact.service;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;


/**
 * @author Thiago
 *
 */
public interface UserGroupService {
	
	/**
	 * @param username
	 * @param group
	 */
	public void createAndAuthenticateUser(String username, String group);

	/**
	 * @return
	 */
	public List<String> listAllGroups();
	
	/**
	 * @param groupName
	 * @return
	 */
	public List<GrantedAuthority> listGroupAuthorities(String groupName);
	
	/**
	 * @param group
	 * @param roles
	 */
	public void addRolesToGroup(String group, String[] roles);

}