#How to use

A video demo can be found on youtube: https://youtu.be/L_OwAbH5coM

And you can try the API your self

the intents are: 
UPDATE, FOLLOWER_COUNT, GET_COMMENTS, GET_NEW, POST 
which can have the following Arguments: 
WHERE, WHAT, SINCE
of course, where those combinations make sense.
EXAMPLE:
http://your-sidekick.herokuapp.com/api/?intent=Follower_count
OR:
http://your-sidekick.herokuapp.com/api/?intent=Follower_count&WHERE=facebook
