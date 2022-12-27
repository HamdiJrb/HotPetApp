const Like = require("../models/Like")

exports.getMy = async (req, res) => {
    let myLikes = await Like.find(
        {
            $or:
                [{liker: req.params.userId},
                    {liked: req.params.userId}]
        }
    ).populate("liker liked")
    res.status(200).json(myLikes)
}

exports.add = async (req, res) => {
    const {likerId, likedId, isRight} = req.body
    let like = await Like.findOne({$and: [{liker: likedId}, {liked: likerId}]})

    if (like) {
        console.log("EXIST")
        if (like.isRight === true) {
            like.isMatch = true
            await like.save()
        }
    } else {
        like = new Like()
        like.liker = likerId
        like.liked = likedId
        like.isRight = isRight
        like.isMatch = false
        await like.save()
    }

    await like.populate("liker liked")
    res.status(200).json(like)
}

exports.delete = async (req, res) => {
    Like.findById(req.params.id).deleteOne().then(_ => {
        return res.status(200).json({message: "Success"})
    }).catch(e => {
        console.log(e)
        return res.status(500).json({message: "Error"})
    });
}