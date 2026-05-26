package resource;


//D Rama Kiron

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/rest")
public class LoginController {

	@GetMapping("/Login")
	public String getLoginPage() {
		return "login";
	}
}
