const bcrypt = require("bcrypt")
const nodemailer = require("nodemailer")
const os = require("os")
const jwt = require("jsonwebtoken")
const User = require("../models/User")

// BASIC ROUTES  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

exports.getAll = async (req, res) => {
    res.status(200).json(await User.find());
}

exports.getOne = async (req, res) => {
    try {
        const user = await User.findById({_id: req.params.userId}).select("-password");
        res.status(200).json(user);
    } catch (err) {
        res.status(500);
    }
};

// AUTH ROUTES - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

exports.register = async (req, res) => {
    const {email, password, username, birthdate, gender, category, about} = req.body

    let imageFilename;
    if (req.file) imageFilename = req.file.filename

    if (await User.findOne({email})) {
        res.status(403).json({message: "User already exist !"})
    } else {
        let user = await new User({
            email,
            password: await bcrypt.hash(password, 10),
            username, birthdate, gender, category, about,
            imageFilename,
            isVerified: false,
        })

        await user.save();

        // token creation
        const token = generateUserToken(user)

        await doSendConfirmationEmail(email, token, req.protocol)

        res.status(200).json(user)
    }
}

exports.login = async (req, res) => {
    const {email, password} = req.body

    email.replace(/\s*/g, "")

    const user = await User.findOne({email})

    if (user && (await bcrypt.compare(password, user.password))) {
        const token = generateUserToken(user)
        console.log(token)

        if (!user.isVerified) {
            console.log("Email not verified")
            res.status(402).json({message: "Email not verified"})
        } else {
            res.status(200).json(user)
        }
    } else {
        console.log("Password or email incorrect")
        res.status(403).json({message: "Password or email incorrect"})
    }
}

exports.loginWithSocial = async (req, res) => {
    const {email} = req.body

    email.replace(/\s*/g, "")

    let user = await User.findOne({email})

    if (!user) {
        user = await new User({
            email,
            password: await bcrypt.hash("00000000", 10),
            username: "unknown_pet", birthdate: Date.now(), gender: "Unspecified", category: "Unspecified",
            isVerified: true,
        })
        await user.save();
    }

    return res.status(200).json(user)
}

exports.confirmation = async (req, res) => {
    let token;
    if (req.params.token) {
        try {
            token = jwt.verify(req.params.token, process.env.JWT_SECRET, null, null);
        } catch (e) {
            return res.render("confirmation.twig", {
                message:
                    "The verification link may have expired, please resend the email.",
            });
        }
    } else {
        return res.render("confirmation.twig", {
            message: "no token",
        });
    }

    User.findById(token.user._id, function (err, user) {
        if (!user) {
            return res.render("confirmation.twig", {
                message: "User does not exist, please register.",
            });
        } else if (user.isVerified) {
            return res.render("confirmation.twig", {
                message: "This user has already been verified, please login",
            });
        } else {
            user.isVerified = true;
            user.save(function (err) {
                if (err) {
                    return res.render("confirmation.twig", {
                        message: err.message,
                    });
                } else {
                    return res.render("confirmation.twig", {
                        message: "Your account has been verified",
                    });
                }
            });
        }
    }).select("-password");
};

exports.updatePreferredParams = async (req, res) => {
    const {email, preferredAgeMin, preferredAgeMax, preferredDistance} = req.body;
    await User.findOneAndUpdate({email}, {
        $set: {
            preferredAgeMin, preferredAgeMax, preferredDistance
        }
    })
    console.log(req.body)
    return res.json(await User.findOne({email}).select("-password"));
};

exports.updateLocation = async (req, res) => {
    const {email, latitude, longitude} = req.body;
    await User.findOneAndUpdate({email}, {$set: {latitude, longitude}})
    return res.json(await User.findOne({email}).select("-password"));
};

exports.updateProfile = async (req, res) => {
    const {email, username, birthdate, gender, category, about} = req.body;
    await User.findOneAndUpdate({email}, {
        $set: {email, username, birthdate, gender, category, about}
    })
    return res.json(await User.findOne({email}).select("-password"));
};

exports.updateProfileImage = async (req, res) => {
    const {email, imageFilename} = req.body;
    await User.findOneAndUpdate({email}, {
        $set: {imageFilename}
    })
    return res.json(imageFilename);
};

exports.addImage = async (req, res) => {
    const {email} = req.body;

    let imageFilename;
    if (req.file) imageFilename = req.file.filename

    await User.findOneAndUpdate({email}, {$push: {images: imageFilename}})

    return res.json(imageFilename);
};

exports.deleteImage = async (req, res) => {
    const {email, imageFilename} = req.body;

    let user = await User.findOneAndUpdate({email}, {$pull: {images: imageFilename}})
    if (user.imageFilename === imageFilename) {
        user.imageFilename = null
        await user.save()
    }

    return res.json(imageFilename);
};

exports.updatePassword = async (req, res) => {
    const {email, password} = req.body;

    try {
        await User.findOneAndUpdate({email},
            {
                $set: {
                    password: await bcrypt.hash(password, 10),
                },
            }
        )
        res.status(200).json({message: "Success"});
    } catch (error) {
        res.status(500).json({error});
    }
}

// FORGOT PASSWORD  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

exports.forgotPassword = async (req, res) => {
    const {email} = req.body;

    const user = await User.findOne({email}).select("-password");

    if (user) {
        const randomNumber = randomIntBetween(1000, 9999);

        // Token creation
        const token = await generateResetToken(randomNumber)

        const success = await sendEmail({
            from: process.env.GMAIL_USER,
            to: email,
            subject: "Password reset - HotPet",
            html:
                "<h3>You have requested to reset your password</h3><p>Your reset code is : <b style='color : #22b7f8'>" +
                randomNumber +
                "</b></p>",
        }).catch((error) => {
            console.log(error)
            return res.status(500).json({
                message: "Error : email could not be sent"
            })
        });

        if (success) {
            console.log("Reset email has been sent to : " + user.email)
            return res.status(200).json(token)
        } else {
            return res.status(500).json({message: "Email could not be sent"})
        }
    } else {
        return res.status(404).json({message: "User does not exist"});
    }
}

exports.verifyResetCode = async (req, res) => {
    const {typedResetCode, token} = req.body;
    console.log(req.body)
    let openToken
    try {
        openToken = jwt.verify(token, process.env.JWT_SECRET, null, null);
    } catch (e) {
        console.log(e)
        return res.status(500).json({message: "Error, could not decrypt token"});
    }

    if (String(openToken.resetCode) === typedResetCode) {
        res.status(200).json({message: "Success"});
    } else {
        res.status(403).json({message: "Incorrect reset code"});
    }
}

// UTILITIES FUNCTIONS  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

function generateUserToken(user) {
    return jwt.sign(
        {user}, process.env.JWT_SECRET, {
            expiresIn: "100000000", // in Milliseconds (3600000 = 1 hour)
        }, {}
    )
}

function generateResetToken(resetCode) {
    return jwt.sign(
        {resetCode},
        process.env.JWT_SECRET, {
            expiresIn: "100000000", // in Milliseconds (3600000 = 1 hour)
        }, {}
    )
}

async function doSendConfirmationEmail(email, token, protocol) {
    let port = process.env.PORT || 5000

    await sendEmail({
        from: process.env.GMAIL_USER,
        to: email,
        subject: "Confirm your email",
        html:
            "<h3>Please confirm your email using this </h3>" +
            "<a href='https://hotpetserver.up.railway.app/user/confirmation/" + token + "'>Link</a>",
    })
}

async function sendEmail(mailOptions) {
    let transporter = await nodemailer.createTransport({
        service: "gmail",
        auth: {
            user: process.env.GMAIL_USER,
            pass: process.env.GMAIL_APP_PASSWORD,
        },
    });

    await transporter.verify(function (error) {
        if (error) {
            console.log(error);
            console.log("Server not ready");
        } else {
            console.log("Server is ready to take our messages");
        }
    })

    await transporter.sendMail(mailOptions, function (error, info) {
        if (error) {
            console.log(error);
            return false;
        } else {
            console.log("Email sent: " + info.response);
            return true;
        }
    });

    return true
}

function randomIntBetween(min, max) {
    return Math.floor(
        Math.random() * (max - min + 1) + min
    )
}
