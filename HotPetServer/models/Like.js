const mongoose = require("mongoose")

const LikeSchema = new mongoose.Schema(
    {
        liker: {type: mongoose.Schema.Types.ObjectId, ref: "User", required: true},
        liked: {type: mongoose.Schema.Types.ObjectId, ref: "User", required: true},
        isRight: {type: Boolean, required: true},
        isMatch: {type: Boolean, required: true},
    },
    {
        timestamps: {currentTime: () => Date.now()},
    }
)
module.exports = mongoose.model("Like", LikeSchema)
