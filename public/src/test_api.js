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

	getSession(false, 1, function(response) {
		console.log(jQuery.parseJSON(response));
	}, function(error) {
		console.log("get session failed");
		console.log(error);
	});

	getUser(true, 1, function(response) {
		console.log(response);
	}, function() {
		console.log("get user failed");
	});

	getSessions(true, "de", function(response) {
		console.log(response);
	}, function() {
		console.log("getsessions onError");
	});

	postMessage(true, 3, 4, "hello whats up?", function(response) {
		console.log(response);
	}, function() {
		console.log("postMessage failed");
	});

});
