# HandlersDemo

This project is a proof of concept that demonstrates a chat communication system between a sender and a receiver using Android's Messenger, Handlers, and Loopers. Both the sender and receiver utilize these concepts in different ways to achieve reactive communication.

## Table of Contents
- [Introduction](#messenger-chat-proof-of-concept)
- [Features](#features)
- [Project Structure](#project-structure)
- [Usage](#usage)
- [Demonstration](#demonstration)
- [Contributing](#contributing)
- [License](#license)

## Features

- Two-way chat communication between a sender and a receiver.
- Sender thread extended from HandlerThread, implementing MessageQueue.IdleHandler.
- Receiver using a Java thread, implementing Runnable and MessageQueue.IdleHandler.
- Use of Messengers for sending and receiving messages.
- Reactive communication where the sender sends a message, and the receiver responds.
- Handling delays and waiting for the receiver to respond.

Demonstration


https://github.com/geetgobindsingh/HandlersDemo/assets/8836820/efb92c9f-cd7e-41ac-88bc-f9f21071ab58



## 🤝🏻 Contribute

Any PRs are very welcome! 😍 You can fix a bug, add a feature, optimize performance and even propose a new cool approach in code-base architecture. Feel free and make a PR! 😌

License
- This project is licensed under the MIT License.


