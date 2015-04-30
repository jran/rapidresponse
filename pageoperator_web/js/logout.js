function logout() {
	Parse.User.logOut();
	window.location = "home.html";
}