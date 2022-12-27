let Message = require("../models/Message")
let Conversation = require("../models/Conversation")

exports.getAllConversations = async (req, res) => {
    res.json(await Conversation.find())
}

exports.getAllMessages = async (req, res) => {
    res.json(await Message.find())
}

exports.getMyConversations = async (req, res) => {
    res.json(await Conversation.find({"sender": req.params.senderId})
        .populate("sender receiver"))
}

exports.getMyMessages = async (req, res) => {
    res.json(await Message.find(
            {
                $or: [
                    {'senderConversation': req.params.conversationId},
                    {'receiverConversation': req.params.conversationId},
                ]
            }
        ).populate([
            {
                path: 'senderConversation',
                populate: [{
                    path: 'sender',
                    model: 'User'
                }, {
                    path: 'receiver',
                    model: 'User'
                }]
            }, {
                path: 'receiverConversation',
                populate: [{
                    path: 'sender',
                    model: 'User'
                }, {
                    path: 'receiver',
                    model: 'User'
                }]
            }
        ])
    )
}

exports.createConversation = async (req, res) => {
    const {sender, receiver} = req.body

    let senderConversation = await Conversation.findOne({
        "sender": sender,
        "receiver": receiver
    })

    if (!senderConversation) {
        senderConversation = new Conversation()
        senderConversation.lastMessage = "New conversation"
        senderConversation.sender = sender
        senderConversation.receiver = receiver
    }
    senderConversation.lastMessageDate = Date()
    senderConversation.save()

    await senderConversation.populate("sender receiver")

    return res.status(200).json(senderConversation)
}

exports.sendMessage = async (req, res) => {
    const {description, senderId, receiverId} = req.body

    let senderConversation = await Conversation.findOne({"sender": senderId, "receiver": receiverId})
    if (!senderConversation) {
        senderConversation = new Conversation()
        senderConversation.sender = senderId
        senderConversation.receiver = receiverId
    }
    senderConversation.lastMessage = description
    senderConversation.lastMessageDate = Date()
    senderConversation.save()

    let receiverConversation = await Conversation.findOne({"sender": receiverId, "receiver": senderId})
    if (!receiverConversation) {
        receiverConversation = new Conversation()
        receiverConversation.sender = receiverId
        receiverConversation.receiver = senderId
    }
    receiverConversation.lastMessage = description
    receiverConversation.lastMessageDate = Date()
    receiverConversation.save()

    const newMessage = new Message()
    newMessage.description = description
    newMessage.senderConversation = senderConversation._id
    newMessage.receiverConversation = receiverConversation._id
    await newMessage.populate("senderConversation receiverConversation")
    console.log(newMessage)
    await newMessage.save()

    res.status(200).json(newMessage)
}

exports.deleteMessage = async (req, res) => {
    await Message.findById(req.params._id).remove();
    res.status(200).json({message: "success"})
}

exports.deleteConversation = async (req, res) => {
    const conversation = await Conversation.findById(req.params.id).remove()
    res.status(200).json(conversation)
}

exports.deleteAll = async (req, res) => {
    Conversation.remove({}, function (err) {
        if (err) {
            return handleError(res, err)
        }
    })
    Message.remove({}, function (err) {
        if (err) {
            return handleError(res, err)
        }
    })

    res.status(204).json({message: "done"})
}