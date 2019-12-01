package com.developer.finalprojectseg3102.controllers;

import com.developer.finalprojectseg3102.dao.CourseDAO;
import com.developer.finalprojectseg3102.dao.SectionDAO;
import com.developer.finalprojectseg3102.dao.TeamDAO;
import com.developer.finalprojectseg3102.dao.UserDAO;
import com.developer.finalprojectseg3102.models.Course;
import com.developer.finalprojectseg3102.models.Section;
import com.developer.finalprojectseg3102.models.Team;
import com.developer.finalprojectseg3102.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

import static com.developer.finalprojectseg3102.controllers.BaseController.isLoggedIn;

@Controller
public class CourseManagementController extends BaseController {

    @RequestMapping(value = "/create-course")
    public String createCourse(@ModelAttribute Course course, Model model, HttpSession session) {
        //Also check if the user is admin: current_user.isAdmin()
        if (isLoggedIn(session)){
            model.addAttribute(course);

//            if (course.getCourseCode() != null){
//                CourseDAO.create(course);
//            }
            return "admin-course";
        }
        else{
            return "login";
        }
    }

    @RequestMapping(value = "/admin-course")
    public String adminCourse(@ModelAttribute Course course, Model model, HttpSession session) throws Exception {
        //Also check if the user is admin: current_user.isAdmin()
        ArrayList<Course> courses = CourseDAO.retrieveCourses();
        model.addAttribute("courses", courses);
        return "admin-course";
    }

    // TODO: Very inefficient code .. it works tho. Might fix it later
    @RequestMapping(value = "/course", params = "section_id")
    public String course(@RequestParam ("section_id") String section_id, @ModelAttribute Section section, Model model, HttpSession session) throws Exception {
        // Check if user has a team
        Boolean hasTeam = false;
        Boolean isStudent = false;

        section = SectionDAO.retrieve(Long.parseLong(section_id));
        model.addAttribute("section", section);
        model.addAttribute("courseInfo", sectionFullName(Long.parseLong(section_id)));
        User current_user = (User) session.getAttribute("user");

        if((current_user.getAccount_type()).equals("professor")){
            isStudent = false;
            hasTeam = false;

            List<Team> sectionTeams = sectionTeams(section.getSection_id());
            model.addAttribute("sectionTeams", sectionTeams);
        }

        // User is a student
        else{
            isStudent = true;
            Team team = studentTeamBasedOnSection(current_user.getUser_id(), section.getSection_id());
            // User has a team
            if(team != null && team.getTeam_id() != 0){
                hasTeam = true;
                model.addAttribute("team", team);
            }

            // user has no team
            else{
                hasTeam = false;
                List<Team> availableTeams = new ArrayList<>();
                List<Team> allTeams = TeamDAO.retrieveTeams();
                for(int i = 0; i<allTeams.size(); i++){

                    int max = (allTeams.get(i)).getMax_capacity();
                    List<User> teamMembers = TeamDAO.retrieveTeamMembers((allTeams.get(i)).getTeam_id());

                    if(max > teamMembers.size()){
                        availableTeams.add(allTeams.get(i));
                    }
                }
                model.addAttribute("availableTeams", availableTeams);
            }
        }

        model.addAttribute("hasTeam", hasTeam);
        model.addAttribute("isStudent", isStudent);
        return "course";
    }


}
