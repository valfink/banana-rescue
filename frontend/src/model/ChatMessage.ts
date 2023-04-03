export type ChatMessage = {
    id: string,
    chatId: string,
    senderId: string,
    timestamp: string,
    content: string,
    comesFromLiveChat?: boolean
}
