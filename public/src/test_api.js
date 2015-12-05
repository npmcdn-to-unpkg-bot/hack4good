jQuery(document).ready(function() {
	// console.log(getSession(4));
	// console.log(getUser(2));

	// doRequest("GET", "http://www.google.de", { 'data' : 'test' }, undefined, function() {
		// console.log("on success");
	// }, function() {
		// console.log("on fail");
	// });
	
	// lang, ownerid, question, topic, onSucces, onError
	// createSession("de", "2", "Could someone pls tell me whats happening to my body?", "My Body", function() {
		// console.log("on success");
	// }, function() {
		// console.log("on error");
	// });
	
	getSession(1,false, function(response) {
		console.log(response);
	}, function(error) {
		console.log("get session failed");
		console.log(error);
	});
	
	getUser(1,true,function(response) {
		console.log(response);
	}, function() {
		console.log("get user failed");
	});
	
	getSessions("de", true, function(response) {
		console.log(response);
	}, function() {
		console.log("getsessions onError");
	});
	
	postMessage(false, 3, 4,"hello whats up?", function(response) {
		console.log(response);
	}, function() {
		console.log("postMessage failed");
	});
	
});
