const mongoose = require("mongoose")

const ConversationSchema = new mongoose.Schema(
    {
        lastMessage: {type: String, required: true},
        lastMessageDate: {type: Date, required: true},
        sender: {
            type: mongoose.Schema.Types.ObjectId,
            ref: "User",
            required: true
        },
        receiver: {
            type: mongoose.Schema.Types.ObjectId,
            ref: "User",
            required: true
        },
    },
    {
        timestamps: {currentTime: () => Date.now()},
    }
)
module.exports = mongoose.model("Conversation", ConversationSchema)
