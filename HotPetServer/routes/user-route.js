const express = require("express")
const router = express.Router()
const upload = require('../middlewares/storage-images');
const controller = require("../controllers/user-controller");

// BASIC
router.route("/").get(controller.getAll)
router.route("/one/:userId").get(controller.getOne);

// AUTH
router.post("/register", upload.single('image'), controller.register);
router.post("/login", controller.login);
router.post("/login-with-social", controller.loginWithSocial);
router.get("/confirmation/:token", controller.confirmation);

// UPDATE
router.put("/update-preferred-params", controller.updatePreferredParams);
router.put("/update-location", controller.updateLocation);
router.put("/update-profile", controller.updateProfile);
router.put("/update-password", controller.updatePassword);
router.put("/update-profile-image", controller.updateProfileImage);
router.put("/add-image", upload.single('image'), controller.addImage);
router.put("/delete-image", controller.deleteImage);

// FORGOT PASSWORD
router.post("/forgot-password", controller.forgotPassword);
router.post("/verify-reset-code", controller.verifyResetCode);

module.exports = router