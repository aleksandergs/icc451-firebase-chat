const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");

admin.initializeApp();

exports.sendChatNotification = onDocumentCreated("/chats/{chatId}/messages/{messageId}", async (event) => {
    const messageData = event.data.data();
    if (!messageData) return null;

    const senderId = messageData.senderId;
    const senderNickname = messageData.senderNickname || "Alguien";
    const messageText = messageData.text || "Te ha enviado un mensaje";

    const chatId = event.params.chatId;
    if (!chatId || !senderId) {
        console.log('Faltan datos críticos. chatId: ${chatId}, senderId: ${senderId}');
        return null;
    }

    const uids = chatId.split("_");

    const recipientId = uids[0] === senderId ? uids[1] : uids[0];

    if (!recipientId || typeof recipientId !== "string" || recipientId.trim() === "") {
        console.log('No se pudo determinar un recipientId válido desde el chatId: "${chatId}"');
        return null;
    }

    try {
        const userDoc = await admin.firestore().collection("users").doc(recipientId).get();
        if (!userDoc.exists) {
            console.log('El usuario receptor ${recipientId} no existe en la colección users.');
            return null;
        }

        const fcmToken = userDoc.data().fcmToken;
        if (!fcmToken) {
            console.log('El usuario receptor ${recipientId} no tiene un fcmToken registrado.');
            return null;
        }

        const payload = {
            token: fcmToken,
            data: {
                nickname: String(senderNickname),
                text: String(messageText),
                otherUserId: String(senderId)
            }
        };

        const response = await admin.messaging().send(payload);
        console.log('Notificación enviada exitosamente al usuario: ${recipientId}');
        return null;

    } catch (error) {
        console.error("Error enviando la notificación de chat:", error);
        return null;
    }
});