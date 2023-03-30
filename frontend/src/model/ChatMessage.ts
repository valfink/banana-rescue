export type ChatMessage = {
    id: string,
    senderId: string,
    timestamp: string,
    content: string,
    comesFromLiveChat?: boolean
}
