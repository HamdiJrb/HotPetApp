const express = require("express")
const router = express.Router()
const controller = require("../controllers/like-controller")

router.route("/").post(controller.add)
router.route("/one/:id").delete(controller.delete)
router.route("/get-my/:userId").get(controller.getMy)

module.exports = router