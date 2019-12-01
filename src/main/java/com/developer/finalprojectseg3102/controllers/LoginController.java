package com.developer.finalprojectseg3102.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.developer.finalprojectseg3102.dao.UserDAO;
import com.developer.finalprojectseg3102.models.Section;
import com.developer.finalprojectseg3102.models.User;

@Controller
public class LoginController extends BaseController {

	@RequestMapping(value = "/login")
	public String getLoginPage(Model model, HttpSession session) {

		if (session.getAttribute("user") == null) {
			return "login";
		} else {
			return "index";
		}
	}

	@RequestMapping(value = "/login-attempt")
	public String showLoginPage(Model model, HttpSession session, @ModelAttribute User user) throws Exception {

		try {
			if (isLoggedIn(session)) {
				return "index";
			} else {
				ArrayList<User> userList = UserDAO.retrieveUsers();

				if (user.getEmail() != null && user.getPassword() != null) {
					for (int i = 0; i < userList.size(); i++) {
						if (user.getEmail().equals(userList.get(i).getEmail())
								&& user.getPassword().equals(userList.get(i).getPassword())) {
							session.setAttribute("loggedIn", true);
							User loggedInUser = new User();
							loggedInUser.setAccount_type(userList.get(i).getAccount_type());
							loggedInUser.setEmail(userList.get(i).getEmail());
							loggedInUser.setFirstName(userList.get(i).getFirstName());
							loggedInUser.setIdentification_number(userList.get(i).getIdentification_number());
							loggedInUser.setLastName(userList.get(i).getLastName());
							loggedInUser.setPassword(userList.get(i).getPassword());
							loggedInUser.setProgram(userList.get(i).getProgram());
							loggedInUser.setUser_id(userList.get(i).getUser_id());
							session.setAttribute("user", loggedInUser);
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// TODO Clean up login check in this method
		if (isLoggedIn(session)) {
			User current_user = (User) session.getAttribute("user");
			model.addAttribute("user", current_user);
			List<Section> sections = getStudentSections(current_user.getUser_id());
			model.addAttribute("sections", sections);
			return "index";
		} else {
			return "login";
		}
	}

	@RequestMapping(value = "/signup")
	public String showSignupPage(Model model, HttpSession session) {

		if (session.getAttribute("user") == null) {
			return "signup";
		} else if (isLoggedIn(session)) {
			return "index";
		} else {
			return "error";
		}
	}

	@RequestMapping(value = "/signup-attempt")
	public String signupAttempt(Model model, HttpSession session, @ModelAttribute User user) {

		try {
			List<User> userList = UserDAO.retrieveUsers();

			for (int i = 0; i < userList.size(); i++) {
				// ideally, the email and id number are not the same as any that exist
				if (user.getEmail() != null && user.getIdentification_number() != null) {
					if (user.getEmail().equals(userList.get(i).getEmail())
							&& (user.getIdentification_number().equals(userList.get(i).getIdentification_number()))) {
						return "signup";
					}
				}
			}

			UserDAO.create(user);
			session.setAttribute("loggedIn", true);
			session.setAttribute("User", user);
			return "index";

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "signup";
		}
	}

	public List<Section> getStudentSections(long user_id) throws Exception {
		return UserDAO.retrieveStudentSections(user_id);
	}
}
