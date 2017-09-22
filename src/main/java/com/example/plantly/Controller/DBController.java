package com.example.plantly.Controller;

import com.example.plantly.Domain.Plant;
import com.example.plantly.Domain.User;
import com.example.plantly.Domain.UserPlant;
import com.example.plantly.Repository.DBRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Controller
public class DBController {
    @Autowired
    private DBRepository DBConnection;

    @GetMapping("/")
    public String homepage() {
        return "index";
    }
    
    @GetMapping("/about")
	public String about() {
		return "about";
	}

    @PostMapping("/signup")
    public ModelAndView signup(Model model, @RequestParam String email, @RequestParam String firstname, @RequestParam String lastname, @RequestParam String password, HttpSession session) {
        List<User> allUsers = DBConnection.getAllUsers();
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getEmail().equals(email)) {
                return new ModelAndView("index").addObject("infoSignup", "User already exists!");
            }
        }
        DBConnection.addUser(email, firstname, lastname, password);
        User user = DBConnection.getCurrentUser(email, password);
        session.setAttribute("user", user);
        return new ModelAndView("userpage");
    }

    @PostMapping("/user")
    public ModelAndView loggedin(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        boolean userExists = DBConnection.userExists(email, password);
        User user = DBConnection.getCurrentUser(email, password);
        if(userExists) {
            session.setAttribute("user", user);
            setSessionUserPlantsList(session);
            return new ModelAndView("userpage");
        }
        return new ModelAndView("index").addObject("infoLogin", "Invalid email or password!");
    }

    private void countdownWateringDays (List<UserPlant> userPlantList){
        ZonedDateTime startOfToday = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
        long todayMs = startOfToday.toEpochSecond() * 1000;
        for(int i=0;i<userPlantList.size();i++){
            if(userPlantList.get(i).regDate.getTime() > todayMs){

            }
        }
    }

    @GetMapping("/user")
    public ModelAndView userpage(HttpSession session) {
        if (session.getAttribute("user") != null) {
            LocalDate from = LocalDate.now();
            long diff = 0;
            setSessionUserPlantsList(session);
            return new ModelAndView("userpage");
        }
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/changePassword")
    public String passwordChangeHTML(){
        return "changePassword";
    }


    @PostMapping("/passwordVerification")
    public /*String*/ ModelAndView passwordVerification(@RequestParam String newPassword, @RequestParam String oldPassword, HttpSession session, Model model) {
        if(session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            if (user.getPassword().equals(oldPassword)) {
                DBConnection.changePassword(user.getUserId(), newPassword);
                return new ModelAndView("changePassword").addObject("info", "Password has been changed");
            } else {
                return new ModelAndView("changePassword").addObject("info", "Incorrect old password!");
            }
        }
        return new ModelAndView("userpage");
    }

    @GetMapping("/logout")
    public ModelAndView logout(HttpSession session, HttpServletResponse res) {
        {
            session.invalidate();
            Cookie cookie = new Cookie("jsessionid", "");
            cookie.setMaxAge(0);
            res.addCookie(cookie);
            return new ModelAndView("redirect:/");
        }
    }

    @GetMapping("/plantinfo/{plantSpecies}")
    public ModelAndView plantinfo(@PathVariable String plantSpecies, HttpSession session) {
        if(session.getAttribute("user") != null) {
            Plant plant = DBConnection.getPlantByPlantSpecies(plantSpecies); // get plant from Plants database using plantSpecies
            return new ModelAndView("plantinfo").addObject("plant", plant);
        }
        return new ModelAndView("index");
    }

    @GetMapping("/addplant")
    public String addplant(){
        return "addplant";
    }


    @PostMapping("/addUserPlant")
    public ModelAndView addUserPlant(@RequestParam String nickName, @RequestParam String plantSpecies, @RequestParam int userId, HttpSession session){
        boolean nickNameExists = DBConnection.nickNameAlreadyExists(nickName, userId);
        LocalDate regdate = LocalDate.now();

        if(!nickNameExists){
            DBConnection.addPlantToUserPlants(nickName, "needs a image URL", userId, plantSpecies, java.sql.Date.valueOf(regdate));
            setSessionUserPlantsList(session);
            return new ModelAndView("userpage");

        }
        return new ModelAndView("userpage").addObject("warning", "Nickname already exists!");
    }

    @GetMapping("/watering/{usersPlantsID}/{waterDays}")
    public String resetWaterDaysLeft(@PathVariable int usersPlantsID, @PathVariable int waterDays){
        LocalDate regdate = LocalDate.now();
        DBConnection.resetWaterDate(usersPlantsID, java.sql.Date.valueOf(regdate), waterDays);
        return "redirect:/user";
    }

    private void setSessionUserPlantsList(HttpSession session){
        User user = (User)session.getAttribute("user");
        List<UserPlant> userPlantList = DBConnection.getUserPlantsInfo(user.getUserId());
        session.setAttribute("userPlantsList", userPlantList);
    }

    @RequestMapping(path = "/GET", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getData(){
        return DBConnection.getPlantName();
    }


    @GetMapping("/deletePlant/{usersPlantsID}")
    public String deletePlant(@PathVariable int usersPlantsID){
        DBConnection.deletePlantFromUserPlants(usersPlantsID);
        return "redirect:/user";
    }

    @GetMapping("/clock")
    public String testClock(){
        return  "clock";
    }


}

