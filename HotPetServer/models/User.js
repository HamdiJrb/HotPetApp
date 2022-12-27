const mongoose = require("mongoose")

const UserSchema = new mongoose.Schema(
    {
        email: {type: String, required: true},
        password: {type: String, required: true},
        username: {type: String, required: true},
        birthdate: {type: Date, required: true},
        gender: {
            type: String,
            enum: {
                values: ['Male', 'Female', 'Unspecified'],
                message: '{VALUE} is not supported'
            },
            required: true
        },
        category: {
            type: String,
            enum: {
                values: ['Cat', 'Dog', 'Horse', 'Unspecified'],
                message: '{VALUE} is not supported'
            },
            required: true
        },
        about: {type: String},

        // PREFERENCES
        preferredAgeMin: {type: Number, default: 1},
        preferredAgeMax: {type: Number, default: 15},
        preferredDistance: {type: Number, default: 50},

        // LOCATION
        latitude: {type: Number},
        longitude: {type: Number},

        // OTHERS
        imageFilename: {type: String},
        images: {type: Array, default: []},
        isVerified: {type: Boolean}
    },
    {
        timestamps: {currentTime: () => Date.now()},
    }
)
module.exports = mongoose.model("User", UserSchema)
