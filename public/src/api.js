jQuery(document).ready(function() {

	var host = "";
	var port = "";

	function getSession(id) {
		// mock

		var sessionObj = {

		};

		return sessionObj;

	};

	function postMessage(sessionId, message) {

	};

	function getDocuments(language) {
		var documents = {
			'data' : [{
				'title' : "this is a title",
				'tags' : [{
					'lang' : 'de',
					'name' : 'language',
					'date' : 1449317640
				}],
				'url' : 'http://refugee-board.de/',
				'date' : 1449317640
			}, {
				'title' : "this is another title",
				'tags' : [{
					'lang' : 'en',
					'name' : 'language',
					'date' : 1449317640
				}],
				'url' : 'http://refugee-board.de/',
				'date' : 1449317640
			},{
				'title' : "this is a title",
				'tags' : [{
					'lang' : 'de',
					'name' : 'language',
					'date' : 1449317640
				}],
				'url' : 'http://refugee-board.de/',
				'date' : 1449317640
			}]
		};
		return documents;
	};

	function doRequest(method, url, postData) {

	};

});
