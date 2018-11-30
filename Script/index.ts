import * as functions from 'firebase-functions';

// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
// export const helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
var admin = require("firebase-admin");
admin.initializeApp();
// Get a database reference to our posts
var db = admin.database();

export const addMessage = functions.https.onCall((data, context) => {
	// Message text passed from the client.
	const object = data.object;
	
	// Checking attribute.
	if (!(typeof object === 'string') || object.length === 0) {
		// Throwing an HttpsError so that the client gets the error details.
		throw new functions.https.HttpsError('invalid-argument', 'The function must be called with ' +
			'one arguments "text" containing the message text to add.');
	}

	// Authentication / user information is automatically added to the request.
	const uid = context.auth.uid;
	const name = context.auth.token.name || null;
	const picture = context.auth.token.picture || null;
	const email = context.auth.token.email || null;
	
	var dura =0;
	var ref = db.ref("object_list");
	ref.orderByChild("object").equalTo(object).on("child_added", function(snapshot) {
		dura = snapshot.val().duration
		console.log(snapshot.val().duration);
	});

	return { duration : dura}
});