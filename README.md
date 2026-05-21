Just a NOTE

TRL Tranformers reinforcment learning
This uses Proximal policy optimization
Which is algorithm in reinforcement learning where model improves gradually without making unstable jumps
Updates the model but not too much at once

DPO (Direct Preference Optimization)

Instead of reinforcement methods ( Rewards and penalties ) This uses comparisions
Winning response and Losing response from Humans
and tries to give rest of responses like winning response
Gradio is Python library that instantly convert ML model into web application UI instantly
Where we can enter text and give images as input if needed

https://colab.research.google.com/drive/1hV6Gcz8vBRS9t0bYkBp6W1ne_yqG6mJx?usp=sharing#scrollTo=629dcca3

=============================================================

Labelling columns is crucial
1) input columns [ main column for which we want to identify ]
2) output columns [ also called label ]

May be change table structure to store questions as text and then labels will be intution, approach , common mistakes
Later we can improve that to questions from text to image and then text extraction from text as it's difficult for codechef to copy text directly and make model understand

-----------------------------------------------------------------------------
Ex: Finetuning BERT and sentimental analysis on twitter comments
1) We are importing dataset( Cleaned comments and output for each comment in this example )
2) Dropping all unrellated columns
3) Here in this example -1,0,1 are used. so we are converting them to 0,1,2 (BERT classification format)
4) Splitting into train and test data
5) Bert finetuning works nicer when we convert dataset into hugging face model from pandas
6) Converting text into tokens and then number so that BERT can read using "bert-base-uncased"
7) Keeping limit so that long tweets get cut off
8) AFter tokenizing we doesn't need old labels so we can remove them
9) Return pytorch tensors
10) We use Padding because every batch as similar length examples
11) we use AutoModelForSequenceClassification so 0 is mapped to negative and 1 to neutral and 2 to positive
12) We calculate metrics (accuracy:- How many predictions went well f1_score: treats all classes equally. so we can have class balance)
13) Epoch means one complete pass of entire "training dataset" through the model
14) We can set how we can train model (evaluate after each epoch, save model after each epoch, learning rate and other details)
15) train() to train
16) evaluate() to validate in test data which we didn't use for training
17) we use classification_report() to get detailed metrics per class
