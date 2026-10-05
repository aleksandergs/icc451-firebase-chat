const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");

admin.initializeApp();

exports.sendChatNotification = onDocumentCreated("chats/{chatId}/messages/{messageId}", async (event) => {
    const messageData = event.data.data();
    if (!messageData) return null;

    const recipientId = messageData.recipientId;
    const senderId = messageData.senderId;
    const senderNickname = messageData.senderNickname || "Alguien";
    const messageText = messageData.text || "Te ha enviado un mensaje";

    try {
        const userDoc = await admin.firestore().collection("users").doc(recipientId).get();
        if (!userDoc.exists) {
            console.log(`El usuario ${recipientId} no existe.`);
            return null;
        }

        const fcmToken = userDoc.data().fcmToken;
        if (!fcmToken) {
            console.log(`El usuario ${recipientId} no tiene un fcmToken registrado.`);
            return null;
        }

        const payload = {
            token: fcmToken,
            data: {
                nickname: senderNickname,
                text: messageText,
                otherUserId: senderId
            },
            notification: {
                title: senderNickname,
                body: messageText
            }
        };

        const response = await admin.messaging().send(payload);
        console.log("Notificación enviada exitosamente:", response);
        return null;

    } catch (error) {
        console.error("Error enviando la notificación de chat:", error);
        return null;
    }
});