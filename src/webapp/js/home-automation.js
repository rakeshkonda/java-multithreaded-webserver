$(function(){

	// Application State
  var state = {
    desiredTemp: 68,
    lightsOn: false,
    curtainsOpen: false
  };  

	// Preload jQuery objects ...
  var lightStatus = $("#lights-status"),
      curtainStatus = $("#curtain-status"),
      tempStatus = $("#temp-status"),      
	  lightsButton = $("#lights"),
      curtainButton = $("#curtain"),
      tempDesired = $("#temp-desired"),  
      tempButton = $("#temp");

	// Event Handler for JSONP call
	function handleResponse(response) {
  	var newState = response.state;
  	console.log("Response: ", newState);
    
    lightStatus.text(newState.lightsOn!='false' ? "lights on" : "lights off");
    lightStatus.toggleClass("filled", newState.lightsOn!='false');
    
    curtainStatus.text(newState.curtainsOpen!='false' ? "curtains open" : "curtains closed");
    curtainStatus.toggleClass("filled", newState.curtainsOpen!='false');
    
    tempStatus.html(newState.desiredTemp + "&deg;F");
    tempDesired.val(newState.desiredTemp);

		// update client state
		state = newState;
  }

	// Send Data via JSONP
	function postState() {
    $.ajax({
      url: "https://jsfiddle.net/echo/jsonp/",
      jsonp: "callback",
      dataType: "jsonp",
      data: {
          state: state
      }, 
      // Work with the response
      success: handleResponse
    });  
  }

	// setup click handlers
	lightsButton.on('click', function(){
  	state.lightsOn = !(state.lightsOn=='true');
    postState();
  });
  
	tempButton.on('click', function(){
  	state.desiredTemp = tempDesired.val();
    postState();
  });
	curtainButton.on('click', function(){
    	state.curtainsOpen = !(state.curtainsOpen=='true');
      postState();
  });
  
  // get the state from server on app load ...
  postState();  
	
});