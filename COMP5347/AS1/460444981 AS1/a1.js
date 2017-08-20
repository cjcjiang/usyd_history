window.onload = function () {
	//TODO:how to get innerHTML with getElementsByClassName
	//TODO:title bu shi xie ti
	document.getElementById("searchButton").addEventListener("click", searchButton);
	document.getElementById("filterButton").addEventListener("click", filterButton);
}

//To display items in this row of the table
function display(rowID) {
	document.getElementById(rowID).style.display = '';
}

//To hide all items in this row of the table
function hide(rowID) {
	document.getElementById(rowID).style.display = 'none';
}

//To highlight items in this row of the table
function makeGreen(inputDiv) {
	inputDiv.style.backgroundColor = "green"
		inputDiv.parentNode.style.backgroundColor = "green";
}

//To change the style to the default
function makeClean(inputDiv) {
	inputDiv.style.backgroundColor = "white"
		inputDiv.parentNode.style.backgroundColor = "#FFFFFF";
}

//This function is called to highlight the paintings with the correct title
//When button "search" is clicked
//For the search keyword, it is case sensitive
function searchButton() {
	var allOfPictures = document.querySelectorAll(".picture");
	//The searching keyword typed by the user
	var searchKeyword = document.getElementById("searchKeyword").value;
	//User does not type any searching keyword, nothing is highlighted
	if (searchKeyword == "") {
		for (var i = 0; i < allOfPictures.length; i++) {
			makeClean(allOfPictures[i]);
		}
	} else {
		for (var i = 0; i < allOfPictures.length; i++) {
			var tempNodelist = allOfPictures[i].querySelectorAll(".title");
			var titleOfPicture = tempNodelist[0].innerHTML;
			//Check if the title of this picture contains the user's keyword
			if (titleOfPicture.indexOf(searchKeyword) != -1) {
				makeGreen(allOfPictures[i]);
			} else {
				makeClean(allOfPictures[i]);
			}
		}
	}
}

//This function is called to adjust the display of the paintings
//When button "filter" is clicked
function filterButton() {
	var allOfPictures = document.querySelectorAll(".picture");
	//The genre selected by the user
	var genreSelected = document.getElementById("filterSelection").value;
	//User want to display all of the genre
	if (genreSelected == "Genre") {
		for (var i = 0; i < allOfPictures.length; i++) {
			display(allOfPictures[i].id);
		}
	} else {
		for (var i = 0; i < allOfPictures.length; i++) {
			var tempNodelist = allOfPictures[i].querySelectorAll(".genre");
			var genreOfPicture = tempNodelist[0].innerHTML;
			//Check if this picture has the same genre with the selected genre
			if (genreOfPicture == genreSelected) {
				display(allOfPictures[i].id);
			} else {
				hide(allOfPictures[i].id);
			}
		}
	}
}
