package roomie.helpers;

import org.orm.PersistentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import roomie.exception.ResourceNotFoundException;
import roomie.models.auth.MyUser;
import roomie.models.house.House;
import roomie.models.landlord.Landlord;
import roomie.models.landlord.LandlordDAO;
import roomie.models.tenant.Tenant;
import roomie.models.tenant.TenantDAO;
import roomie.services.HouseService;
import roomie.services.LandlordService;

/**
 * @author: Vasco Ramos
 * @created: 14/04/2021 - 17:06
 */

@Component("userSecurity")
public class UserSecurity {
	@Autowired
	private HouseService houseService;
	
	@Autowired
	private LandlordService landlordService;
	
	public boolean isSelf(Authentication authentication, int userId) throws PersistentException {
		String email = ((UserDetails) authentication.getPrincipal()).getUsername();
		
		Tenant[] tenants = TenantDAO.listTenantByQuery("Email='" + email + "'", null);
		if (tenants.length > 0) {
			return tenants[0].getId() == userId;
		}
		
		Landlord[] landlords = LandlordDAO.listLandlordByQuery("Email='" + email + "'", null);
		if (landlords.length > 0) {
			return landlords[0].getId() == userId;
		}
		
		return false;
	}
	
	public boolean isOwner(Authentication authentication, int houseId) throws PersistentException, ResourceNotFoundException {
		Landlord landlord = landlordService.getById(((MyUser) authentication.getPrincipal()).getId());
		House house = houseService.getById(houseId);
		return landlord.houses.contains(house);
	}
}