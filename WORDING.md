# How to submit

Create a zip file and upload it using the submission link present in the e-mail
that had this project attached. Please make sure to include all your source
files and your git folders with it.

# Add a new API endpoint

Add a new endpoint under `/orders/:courierId` to retrieve the list of orders
that are available for a particular courier. The format should be the same
one you can find in the endpoint `/orders`, which is alredy implemented.

## Hide

Not every courier can see every order.

- If the description of the order contains the words pizza, cake or flamingo,
we can only show the order to the courier if they are equipped with a Glovo box.

- If the order is further than 5km to the courier, we will only show it to
couriers that move in motorcycle or electric scooter.

## Prioritise

We want to prioritise some orders in order to improve our Customer Experience.

- We'll show the orders that are close to the courier first, in slots of 500
meters (e.g. orders closer than 500m have the same priority; same orders
between 500 and 1000m)

- Inside each slot, we'll show the orders that belong to a VIP customer first, sorted by distance

- Then, in each slot, we'll show the orders that are food, sorted by distance

- ...and the rest of orders in the slot, sorted by distance

## Experiment

At Glovo we're always trying to improve how we do things, so we are going
to experiment a bit with how we filter and sort the orders we show to the
couriers.

- Make sure that all the distance parameters are configurable via
`application.properties`.

- Make sure that the words that make the order to require a box are
configurable as well.

- Make sure we can configure the order in which the priorities are applied
(e.g. we should be able to put VIP before food, or food before close orders
just by changing the configuration)

## What we'll value

- Clean, succinct code that works without bugs
- Good test coverage
- Good Object Orientation practices
- Adherence to SOLID principles

## Notes

- Take advantage of the provided classes
- Ideally, you shouldn't need to modify any of the provided classes

