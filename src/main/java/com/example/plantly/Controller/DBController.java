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

    @PostMapping("/signup") //When signup is pressed in the index page the information in the forms are sent here, we use this in the repository to check if user exists. If this is a new user we add the user to the user table by the repository and we logg in.
    public ModelAndView signup(@RequestParam String email, @RequestParam String firstname, @RequestParam String lastname, @RequestParam String password, HttpSession session) {
        email = email.toLowerCase();
            if (!DBConnection.userExists(email)) {
                User user = new User(firstname, lastname, email, password);
                DBConnection.addUser(user);
                return loggin(email, password, session);
            }
            return new ModelAndView("index").addObject("infoSignup", "User already exists!");
    }

    @PostMapping("/user") //When login in is pressed in the index page email and password from the forms are sent to the repository to check if user exists
    public ModelAndView loggin(@RequestParam String email, @RequestParam String password, HttpSession session) {
        email = email.toLowerCase(); // so the email form is non case sensitive
        User user = DBConnection.checkUser(email, password);
        if(user != null) {
            session.setAttribute("user", user);
            setSessionUserPlantsList(session);
            return new ModelAndView("userpage");
        }
        return new ModelAndView("index").addObject("infoLogin", "Invalid email or password!");
    }

    @GetMapping("/user")
    public ModelAndView userpage(HttpSession session) {
        if (session.getAttribute("user") != null) {
            setSessionUserPlantsList(session);
            return new ModelAndView("userpage");
        }
        return new ModelAndView("redirect:/");
    }

    @GetMapping("/changePassword")
    public String passwordChangeHTML(HttpSession session){
        if (session.getAttribute("user") != null) {
            return "changePassword";
        }
        return "index";
    }

    @GetMapping("/admin")
    public String adminHTML(HttpSession session){
        try {
            User user = (User) session.getAttribute("user");
            if (user.getUserType().equals("admin")) {
                return "admin";
            }
            return "index";
        }catch(NullPointerException e){
            return "index";
        }
    }


    @PostMapping("/passwordVerification") // when pressing the change password button in changePassword page the new and old password entered in the form are sent here
    public  ModelAndView passwordVerification(@RequestParam String newPassword, @RequestParam String oldPassword, HttpSession session) {
        if(session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            if (user.getPassword().equals(oldPassword)) { //first a check if the old password is typed correctly by using the password stored i sessions
                DBConnection.changePassword(user.getUserId(), newPassword); // if the old password matches the new password is inserted to the user table by the repository
                return new ModelAndView("changePassword").addObject("info", "Password has been changed");
            } else {
                return new ModelAndView("changePassword").addObject("info", "Incorrect old password!");
            }
        }
        return new ModelAndView("userpage");
    }

    @GetMapping("/logout") // killing all sessions and redirects to index by getmapping: /
    public ModelAndView logout(HttpSession session, HttpServletResponse res) {
        {
            session.invalidate();
            Cookie cookie = new Cookie("jsessionid", "");
            cookie.setMaxAge(0);
            res.addCookie(cookie);
            return new ModelAndView("redirect:/");
        }
    }

    @GetMapping("/plantinfo/{plantSpecies}") // when pressing the href in userpage for a plant the plant species is sent here so we can extract the correct plant information from the database
    public ModelAndView plantinfo(@PathVariable String plantSpecies, HttpSession session) {
        if(session.getAttribute("user") != null) {
            Plant plant = DBConnection.getPlantByPlantSpecies(plantSpecies); // get plant from Plants database using plantSpecies
            return new ModelAndView("plantinfo").addObject("plant", plant);
        }
        return new ModelAndView("index");
    }

    @PostMapping("/addUserPlant") // when pressing the add plant in userPage the nickname and plantspecies from the forms are sent here
    public ModelAndView addUserPlant(@RequestParam String nickName, @RequestParam String plantSpecies, @RequestParam int userId, HttpSession session){
        boolean nickNameExists = DBConnection.nickNameAlreadyExists(nickName, userId); // A check that this specific users nickname is uniq for this user
        LocalDate regdate = LocalDate.now(); // to calculate the watering date
        if(!nickNameExists){
            DBConnection.addPlantToUserPlants(nickName, "needs a image URL", userId, plantSpecies, java.sql.Date.valueOf(regdate)); // In the database regDate is a date, I´m looking into changing this and do the calculation in the SQL instead
            setSessionUserPlantsList(session);
            return new ModelAndView("userpage");

        }
        return new ModelAndView("userpage").addObject("warning", "Nickname already exists!");
    }

    @GetMapping("/watering/{usersPlantsID}/{waterDays}") //When pressing the watering button in userPage the water sequence (waterDays) to set next watering date in the repostitory. I´m also looking into moving this calculation to the SQL
    public String resetWaterDaysLeft(@PathVariable int usersPlantsID, @PathVariable int waterDays){
        LocalDate regdate = LocalDate.now();
        DBConnection.resetWaterDate(usersPlantsID, java.sql.Date.valueOf(regdate), waterDays);
        return "redirect:/user";
    }

    private void setSessionUserPlantsList(HttpSession session){ // this is where we set the users plants list in sessions by getting the information from the users plants table.
        User user = (User)session.getAttribute("user");
        List<UserPlant> userPlantList = DBConnection.getUserPlantsInfo(user.getUserId());
        if(userPlantList.isEmpty()){
            userPlantList.add(new UserPlant(0, "empty")); // So we now what to show in the userPage, if the user hasn't stored any plants we use thymeleaf to hide that form and the table blanc
        }
        session.setAttribute("userPlantsList", userPlantList);
    }

    @PostMapping("/addPlantToPlants") //When add plant is pressed in the admin page all the information in the forms are sent here and inserted to a plant object which is sent to the repository that inserts the information to the plants table
    public ModelAndView addPlantToPlantsTable(String plantSpecies, String plantGenus, String plantInfo, String wateringInfo, String tempature,
                                              String humidity, String flowering, String pests, String diseases, String soil, String potSize,
                                              String poisonous, int wateringDays, String fertilizing, String lightinfo, String lightNeeded){

        Plant plant = new Plant(plantSpecies, plantGenus, plantInfo, wateringInfo, tempature,
                                humidity, flowering, pests, diseases, soil, potSize,
                                poisonous, wateringDays, fertilizing, lightinfo, lightNeeded);
        boolean insert = DBConnection.addPlantToPlants(plant);
        if(insert){
            return new ModelAndView("admin").addObject("info", "Plant added!");
        }else {
            return new ModelAndView("admin").addObject("info", "Couldn´t add plant!");
        }
    }


    @GetMapping("/deletePlant/{usersPlantsID}") // When the x-button is pressed in the user page we use the users plantsID that is stored in the session to delete this plant from users plants table
    public String deletePlant(@PathVariable int usersPlantsID, HttpSession session){
        DBConnection.deletePlantFromUserPlants(usersPlantsID); // The plant is deleted from the repository
        setSessionUserPlantsList(session); // The session for the user plants list is updated
        return "redirect:/user";
    }

    @RequestMapping(path = "/GET", method = RequestMethod.GET) //To populate the plants in the plant species form in add plant
    @ResponseBody
    public List<String> getData(){
        return DBConnection.getPlantName(); //putting all the plant species from the plants table in a list so the form can show them when typing a letter/char
    }

    @GetMapping("/clock") // A count down clock we want to use by JS to count down the days and hours to water the plant next to water
    public String testClock(){
        return  "clock";
    }


}

